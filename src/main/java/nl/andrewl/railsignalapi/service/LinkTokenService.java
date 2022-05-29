package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.LinkTokenRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.live.ComponentDownlinkService;
import nl.andrewl.railsignalapi.model.LinkToken;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.model.component.ComponentType;
import nl.andrewl.railsignalapi.rest.dto.link_token.LinkTokenCreatedResponse;
import nl.andrewl.railsignalapi.rest.dto.link_token.LinkTokenPayload;
import nl.andrewl.railsignalapi.rest.dto.link_token.LinkTokenResponse;
import nl.andrewl.railsignalapi.rest.dto.link_token.StandaloneLinkTokenResponse;
import nl.andrewl.railsignalapi.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LinkTokenService {
	private final LinkTokenRepository tokenRepository;
	private final RailSystemRepository railSystemRepository;
	private final ComponentRepository<Component> componentRepository;
	private final PasswordEncoder passwordEncoder;
	private final ComponentDownlinkService componentDownlinkService;

	@Transactional
	public LinkTokenCreatedResponse createToken(long rsId, LinkTokenPayload payload) {
		var rs = railSystemRepository.findById(rsId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (tokenRepository.existsByLabel(payload.label())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is already a token with that label.");
		}
		final Set<ComponentType> validTypes = Set.of(ComponentType.SIGNAL, ComponentType.SWITCH, ComponentType.SEGMENT_BOUNDARY);
		Set<Component> components = new HashSet<>();
		for (var cId : payload.componentIds()) {
			var c = componentRepository.findByIdAndRailSystemId(cId, rsId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component with id " + cId + " was not found in this rail system."));
			if (!validTypes.contains(c.getType())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component with id " + cId + " is not a valid type which allows linking.");
			}
			components.add(c);
		}
		final String token = StringUtils.randomString(32, StringUtils.ALPHA_NUM);
		tokenRepository.save(new LinkToken(
				rs,
				payload.label(),
				token.substring(0, LinkToken.PREFIX_SIZE),
				passwordEncoder.encode(token),
				components
		));
		return new LinkTokenCreatedResponse(token);
	}

	@Transactional(readOnly = true)
	public Optional<LinkToken> validateToken(String rawToken) {
		for (var token : tokenRepository.findAllByTokenPrefix(rawToken.substring(0, LinkToken.PREFIX_SIZE))) {
			if (passwordEncoder.matches(rawToken, token.getTokenHash())) {
				return Optional.of(token);
			}
		}
		return Optional.empty();
	}

	@Transactional(readOnly = true)
	public List<LinkTokenResponse> getTokens(long rsId) {
		var rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return tokenRepository.findAllByRailSystem(rs).stream()
				.sorted(Comparator.comparing(LinkToken::getLabel))
				.map(LinkTokenResponse::new).toList();
	}

	@Transactional
	public void deleteToken(long rsId, long ltId) {
		var token = tokenRepository.findByIdAndRailSystemId(ltId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		componentDownlinkService.deregisterDownlink(token.getId());
		tokenRepository.delete(token);
	}

	@Transactional(readOnly = true)
	public StandaloneLinkTokenResponse getToken(String rawToken) {
		return new StandaloneLinkTokenResponse(validateToken(rawToken).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}
}
