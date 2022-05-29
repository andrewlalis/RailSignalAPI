package nl.andrewl.railsignalapi.rest;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.rest.dto.link_token.StandaloneLinkTokenResponse;
import nl.andrewl.railsignalapi.service.LinkTokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/lt/{token}")
@RequiredArgsConstructor
public class StandaloneLinkTokenApiController {
	private final LinkTokenService tokenService;

	@GetMapping
	public StandaloneLinkTokenResponse getToken(@PathVariable String token) {
		return tokenService.getToken(token);
	}
}
