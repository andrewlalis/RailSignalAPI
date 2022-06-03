package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.dao.SegmentRepository;
import nl.andrewl.railsignalapi.live.ComponentDownlinkService;
import nl.andrewl.railsignalapi.live.dto.ErrorMessage;
import nl.andrewl.railsignalapi.live.dto.SegmentBoundaryUpdateMessage;
import nl.andrewl.railsignalapi.live.dto.SegmentStatusMessage;
import nl.andrewl.railsignalapi.live.websocket.AppUpdateService;
import nl.andrewl.railsignalapi.model.Segment;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.model.component.SegmentBoundaryNode;
import nl.andrewl.railsignalapi.rest.dto.FullSegmentResponse;
import nl.andrewl.railsignalapi.rest.dto.SegmentPayload;
import nl.andrewl.railsignalapi.rest.dto.SegmentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SegmentService {
	private final SegmentRepository segmentRepository;
	private final RailSystemRepository railSystemRepository;
	private final ComponentRepository<Component> componentRepository;
	private final ComponentRepository<SegmentBoundaryNode> segmentBoundaryRepository;

	private final ComponentDownlinkService downlinkService;
	private final AppUpdateService appUpdateService;

	@Transactional(readOnly = true)
	public List<SegmentResponse> getSegments(long rsId) {
		return segmentRepository.findAllByRailSystemIdOrderByName(rsId).stream().map(SegmentResponse::new).toList();
	}

	@Transactional(readOnly = true)
	public FullSegmentResponse getSegment(long rsId, long segmentId) {
		var segment = segmentRepository.findByIdAndRailSystemId(segmentId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return new FullSegmentResponse(segment);
	}

	@Transactional
	public FullSegmentResponse create(long rsId, SegmentPayload payload) {
		var rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		String name = payload.name();
		if (segmentRepository.existsByNameAndRailSystem(name, rs)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Segment with that name already exists.");
		}
		Segment segment = segmentRepository.save(new Segment(rs, name));
		return new FullSegmentResponse(segment);
	}

	@Transactional
	public void remove(long rsId, long segmentId) {
		var segment = segmentRepository.findByIdAndRailSystemId(segmentId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		componentRepository.deleteAll(segment.getSignals());
		componentRepository.deleteAll(segment.getBoundaryNodes());
		segmentRepository.delete(segment);
	}

	private void sendSegmentOccupiedStatus(Segment segment) {
		for (var signal : segment.getSignals()) {
			downlinkService.sendMessage(signal.getId(), new SegmentStatusMessage(signal.getId(), segment.getId(), segment.isOccupied()));
			appUpdateService.sendComponentUpdate(segment.getRailSystem().getId(), signal.getId());
		}
	}

	@Transactional
	public FullSegmentResponse toggleOccupied(long rsId, long sId) {
		var segment = segmentRepository.findByIdAndRailSystemId(sId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		segment.setOccupied(!segment.isOccupied());
		segmentRepository.save(segment);
		sendSegmentOccupiedStatus(segment);
		return new FullSegmentResponse(segment);
	}

	/**
	 * Handles updates from segment boundary components.
	 * @param msg The update message.
	 */
	@Transactional
	public void onBoundaryUpdate(SegmentBoundaryUpdateMessage msg) {
		var segmentBoundary = segmentBoundaryRepository.findById(msg.cId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		segmentRepository.findByIdAndRailSystemId(msg.toSegmentId, segmentBoundary.getRailSystem().getId())
			.ifPresentOrElse(
				segment -> handleSegmentBoundaryMessage(msg.eventType, segmentBoundary, segment),
				() -> downlinkService.sendMessage(new ErrorMessage(msg.cId, "Invalid toSegmentId."))
			);
	}

	private void handleSegmentBoundaryMessage(
			SegmentBoundaryUpdateMessage.Type type,
			SegmentBoundaryNode segmentBoundary,
			Segment toSegment
	) {
		switch (type) {
			case ENTERING -> {
				log.info("Train entering segment {} in rail system {}.", toSegment.getName(), segmentBoundary.getRailSystem().getName());
				for (var segment : segmentBoundary.getSegments()) {
					if (!segment.isOccupied()) {
						segment.setOccupied(true);
						segmentRepository.save(segment);
					}
					sendSegmentOccupiedStatus(segment);
				}
			}
			case ENTERED -> {
				log.info("Train has entered segment {} in rail system {}.", toSegment.getName(), segmentBoundary.getRailSystem().getName());
				List<Segment> otherSegments = new ArrayList<>(segmentBoundary.getSegments());
				// Set the "to" segment as occupied.
				toSegment.setOccupied(true);
				segmentRepository.save(toSegment);
				otherSegments.remove(toSegment);
				// And all others as no longer occupied.
				for (var segment : otherSegments) {
					log.info("Train has left segment {} in rail system {}.", segment.getName(), segmentBoundary.getRailSystem().getName());
					segment.setOccupied(false);
					segmentRepository.save(segment);
					sendSegmentOccupiedStatus(segment);
				}
			}
		}
	}
}
