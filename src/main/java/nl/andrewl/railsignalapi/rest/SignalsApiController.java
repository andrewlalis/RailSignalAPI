package nl.andrewl.railsignalapi.rest;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.rest.dto.SignalCreationPayload;
import nl.andrewl.railsignalapi.rest.dto.SignalResponse;
import nl.andrewl.railsignalapi.service.SignalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/railSystems/{rsId}/signals")
@RequiredArgsConstructor
public class SignalsApiController {
	private final SignalService signalService;

	@PostMapping
	public SignalResponse createSignal(@PathVariable long rsId, @RequestBody SignalCreationPayload payload) {
		return signalService.createSignal(rsId, payload);
	}

	@GetMapping
	public List<SignalResponse> getSignals(@PathVariable long rsId) {
		return signalService.getAllSignals(rsId);
	}

	@GetMapping(path = "/{sigId}")
	public SignalResponse getSignal(@PathVariable long rsId, @PathVariable long sigId) {
		return signalService.getSignal(rsId, sigId);
	}

	@DeleteMapping(path = "/{sigId}")
	public ResponseEntity<?> deleteSignal(@PathVariable long rsId, @PathVariable long sigId) {
		signalService.deleteSignal(rsId, sigId);
		return ResponseEntity.noContent().build();
	}
}
