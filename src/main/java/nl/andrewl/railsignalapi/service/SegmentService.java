package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.dao.SegmentRepository;
import nl.andrewl.railsignalapi.live.ComponentDownlinkService;
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
			downlinkService.sendMessage(signal.getId(), new SegmentStatusMessage(signal.getId(), segment.isOccupied()));
			appUpdateService.sendComponentUpdate(segment.getRailSystem().getId(), signal.getId());
		}
	}

	@Transactional
	public void onBoundaryUpdate(SegmentBoundaryUpdateMessage msg) {
		var segmentBoundary = segmentBoundaryRepository.findById(msg.cId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		switch (msg.eventType) {
			case ENTERING -> {
				for (var segment : segmentBoundary.getSegments()) {
					segment.setOccupied(true);
				}
				segmentRepository.saveAll(segmentBoundary.getSegments());
			}
			case ENTERED -> {
				List<Segment> otherSegments = new ArrayList<>(segmentBoundary.getSegments());
				// Set the "to" segment as occupied.
				segmentRepository.findById(msg.toSegmentId).ifPresent(segment -> {
					segment.setOccupied(true);
					segmentRepository.save(segment);
					sendSegmentOccupiedStatus(segment);
					otherSegments.remove(segment);
				});
				// And all others as no longer occupied.
				for (var segment : otherSegments) {
					segment.setOccupied(false);
					segmentRepository.save(segment);
					sendSegmentOccupiedStatus(segment);
				}
			}
		}
	}
}
