package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
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
}
