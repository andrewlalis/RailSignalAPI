package nl.andrewl.railsignalapi.rest;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.rest.dto.RailSystemSettingsPayload;
import nl.andrewl.railsignalapi.service.RailSystemSettingsService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/rs/{rsId}/settings")
@RequiredArgsConstructor
public class RailSystemSettingsApiController {
	private final RailSystemSettingsService settingsService;

	@GetMapping
	public RailSystemSettingsPayload getSettings(@PathVariable long rsId) {
		return settingsService.getSettings(rsId);
	}

	@PostMapping
	public RailSystemSettingsPayload updateSettings(@PathVariable long rsId, @RequestBody @Valid RailSystemSettingsPayload payload) {
		return settingsService.setSettings(rsId, payload);
	}
}
