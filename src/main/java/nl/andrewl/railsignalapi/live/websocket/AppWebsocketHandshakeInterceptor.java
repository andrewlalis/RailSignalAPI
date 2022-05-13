package nl.andrewl.railsignalapi.live.websocket;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * This interceptor is used to check incoming websocket connections from the
 * web app, to ensure that they're directed towards a valid rail system.
 */
@Component
@RequiredArgsConstructor
public class AppWebsocketHandshakeInterceptor implements HandshakeInterceptor {
	private final RailSystemRepository railSystemRepository;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
		String path = request.getURI().getPath();
		Long railSystemId = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
		if (!railSystemRepository.existsById(railSystemId)) {
			response.setStatusCode(HttpStatus.NOT_FOUND);
			return false;
		}
		attributes.put("railSystemId", railSystemId);
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
		// Nothing to do after the handshake.
	}
}
