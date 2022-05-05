package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.dao.SegmentRepository;
import nl.andrewl.railsignalapi.model.RailSystem;
import nl.andrewl.railsignalapi.rest.dto.RailSystemCreationPayload;
import nl.andrewl.railsignalapi.rest.dto.RailSystemResponse;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RailSystemService {
	private final RailSystemRepository railSystemRepository;
	private final SegmentRepository segmentRepository;
	private final ComponentRepository componentRepository;

	@Transactional
	public List<RailSystemResponse> getRailSystems() {
		return railSystemRepository.findAll(Sort.by("name")).stream().map(RailSystemResponse::new).toList();
	}

	@Transactional
	public RailSystemResponse createRailSystem(RailSystemCreationPayload payload) {
		if (railSystemRepository.existsByName(payload.name())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A rail system with that name already exists.");
		}
		RailSystem rs = new RailSystem(payload.name());
		return new RailSystemResponse(railSystemRepository.save(rs));
	}

	@Transactional
	public void delete(long rsId) {
		var rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		componentRepository.deleteAllByRailSystem(rs);
		segmentRepository.deleteAllByRailSystem(rs);
		railSystemRepository.delete(rs);
	}

	@Transactional(readOnly = true)
	public RailSystemResponse getRailSystem(long rsId) {
		var rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return new RailSystemResponse(rs);
	}
}
