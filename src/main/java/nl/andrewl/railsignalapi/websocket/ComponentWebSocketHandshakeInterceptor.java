package nl.andrewl.railsignalapi.websocket;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ComponentWebSocketHandshakeInterceptor implements HandshakeInterceptor {
	private final RailSystemRepository railSystemRepository;
	private final ComponentRepository<nl.andrewl.railsignalapi.model.component.Component> componentRepository;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		String[] queryParams = request.getURI().getQuery().split("&");
		System.out.println(Arrays.toString(queryParams));
		return false;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

	}
}
