package nl.andrewl.railsignalapi.live.websocket;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.LinkTokenRepository;
import nl.andrewl.railsignalapi.model.LinkToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * An interceptor that checks incoming component websocket connections to
 * ensure that they have a required "token" query parameter that refers to one
 * or more components in a rail system. If the token is valid, we pass its id
 * on as an attribute for the handler that will register the connection.
 */
@Component
@RequiredArgsConstructor
public class ComponentWebsocketHandshakeInterceptor implements HandshakeInterceptor {
	private final LinkTokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		String query = request.getURI().getQuery();
		int tokenIdx = query.lastIndexOf("token=");
		if (tokenIdx == -1) {
			response.setStatusCode(HttpStatus.BAD_REQUEST);
			return false;
		}
		String rawToken = query.substring(tokenIdx + "token=".length());
		if (rawToken.length() < LinkToken.PREFIX_SIZE) {
			response.setStatusCode(HttpStatus.BAD_REQUEST);
			return false;
		}
		Iterable<LinkToken> tokens = tokenRepository.findAllByTokenPrefix(rawToken.substring(0, LinkToken.PREFIX_SIZE));
		for (var token : tokens) {
			if (passwordEncoder.matches(rawToken, token.getTokenHash())) {
				attributes.put("tokenId", token.getId());
				return true;
			}
		}
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		return false;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
		// Don't need to do anything after the handshake.
	}
}
