package nl.andrewl.railsignalapi.live.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuration for Rail Signal's websockets. This includes both app and
 * component connections.
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
@Slf4j
public class WebsocketConfig implements WebSocketConfigurer {
	private final ComponentWebsocketHandler componentHandler;
	private final ComponentWebsocketHandshakeInterceptor componentInterceptor;
	private final AppWebsocketHandler appHandler;
	private final AppWebsocketHandshakeInterceptor appInterceptor;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(componentHandler, "/api/ws/component")
				.setAllowedOrigins("*")
				.addInterceptors(componentInterceptor);
		WebSocketHandlerRegistration appHandlerReg = registry.addHandler(appHandler, "/api/ws/app/*")
				.addInterceptors(appInterceptor);
		appHandlerReg.setAllowedOrigins("*");
	}
}
