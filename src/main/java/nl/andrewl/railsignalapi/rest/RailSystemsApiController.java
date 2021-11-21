package nl.andrewl.railsignalapi.rest;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.rest.dto.RailSystemCreationPayload;
import nl.andrewl.railsignalapi.rest.dto.RailSystemResponse;
import nl.andrewl.railsignalapi.service.RailSystemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/railSystems")
@RequiredArgsConstructor
public class RailSystemsApiController {
	private final RailSystemService railSystemService;

	@GetMapping
	public List<RailSystemResponse> getRailSystems() {
		return railSystemService.getRailSystems();
	}

	@PostMapping
	public RailSystemResponse createRailSystem(@RequestBody RailSystemCreationPayload payload) {
		return railSystemService.createRailSystem(payload);
	}
}
