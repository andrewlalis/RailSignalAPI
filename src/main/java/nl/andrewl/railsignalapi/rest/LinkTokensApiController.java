package nl.andrewl.railsignalapi.rest;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.rest.dto.LinkTokenCreatedResponse;
import nl.andrewl.railsignalapi.rest.dto.LinkTokenPayload;
import nl.andrewl.railsignalapi.rest.dto.LinkTokenResponse;
import nl.andrewl.railsignalapi.service.LinkTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for endpoints regarding link tokens.
 */
@RestController
@RequestMapping(path = "/api/rs/{rsId}/lt")
@RequiredArgsConstructor
public class LinkTokensApiController {
	private final LinkTokenService tokenService;

	@GetMapping
	public List<LinkTokenResponse> getTokens(@PathVariable long rsId) {
		return tokenService.getTokens(rsId);
	}

	@PostMapping
	public LinkTokenCreatedResponse createToken(@PathVariable long rsId, @RequestBody @Valid LinkTokenPayload payload) {
		return tokenService.createToken(rsId, payload);
	}

	@DeleteMapping(path = "/{ltId}")
	public ResponseEntity<Void> deleteToken(@PathVariable long rsId, @PathVariable long ltId) {
		tokenService.deleteToken(rsId, ltId);
		return ResponseEntity.noContent().build();
	}
}
