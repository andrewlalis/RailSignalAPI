package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.dao.SegmentRepository;
import nl.andrewl.railsignalapi.model.Segment;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.rest.SegmentPayload;
import nl.andrewl.railsignalapi.rest.dto.FullSegmentResponse;
import nl.andrewl.railsignalapi.rest.dto.SegmentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SegmentService {
	private final SegmentRepository segmentRepository;
	private final RailSystemRepository railSystemRepository;
	private final ComponentRepository<Component> componentRepository;

	@Transactional(readOnly = true)
	public List<SegmentResponse> getSegments(long rsId) {
		return segmentRepository.findAllByRailSystemId(rsId).stream().map(SegmentResponse::new).toList();
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
	}
}
