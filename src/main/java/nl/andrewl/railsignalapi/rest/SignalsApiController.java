package nl.andrewl.railsignalapi.rest;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.rest.dto.SignalCreationPayload;
import nl.andrewl.railsignalapi.rest.dto.SignalResponse;
import nl.andrewl.railsignalapi.service.SignalService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/railSystems/{rsId}/signals")
@RequiredArgsConstructor
public class SignalsApiController {
	private final SignalService signalService;

	@PostMapping
	public SignalResponse createSignal(@PathVariable long rsId, @RequestBody SignalCreationPayload payload) {
		return signalService.createSignal(rsId, payload);
	}
}
