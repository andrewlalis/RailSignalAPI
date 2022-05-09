package nl.andrewl.railsignalapi.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ComponentWebSocketConfig implements WebSocketConfigurer {
	private final SignalWebSocketHandler webSocketHandler;
	private final ComponentWebSocketHandshakeInterceptor handshakeInterceptor;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketHandler, "/api/ws/component")
				.addInterceptors(handshakeInterceptor);
	}
}
