package nl.andrewl.railsignalapi.rest;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.rest.dto.PathNodeUpdatePayload;
import nl.andrewl.railsignalapi.rest.dto.component.in.ComponentPayload;
import nl.andrewl.railsignalapi.rest.dto.component.out.ComponentResponse;
import nl.andrewl.railsignalapi.service.ComponentCreationService;
import nl.andrewl.railsignalapi.service.ComponentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/rs/{rsId}/c")
@RequiredArgsConstructor
public class ComponentsApiController {
	private final ComponentService componentService;
	private final ComponentCreationService componentCreationService;

	@GetMapping
	public List<ComponentResponse> getAllComponents(@PathVariable long rsId) {
		return componentService.getComponents(rsId);
	}

	@GetMapping(path = "/{cId}")
	public ComponentResponse getComponent(@PathVariable long rsId, @PathVariable long cId) {
		return componentService.getComponent(rsId, cId);
	}

	@PostMapping
	public ComponentResponse createComponent(@PathVariable long rsId, @RequestBody ComponentPayload payload) {
		return componentCreationService.create(rsId, payload);
	}

	@DeleteMapping(path = "/{cId}")
	public ResponseEntity<Void> removeComponent(@PathVariable long rsId, @PathVariable long cId) {
		componentService.removeComponent(rsId, cId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping(path = "/{cId}/connectedNodes")
	public ComponentResponse updateConnectedNodes(@PathVariable long rsId, @PathVariable long cId, @RequestBody PathNodeUpdatePayload payload) {
		return componentService.updatePath(rsId, cId, payload);
	}
}
