package nl.andrewl.railsignalapi.live.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * A websocket handler for all websocket connections to the Rail Signal web
 * app frontend.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppWebsocketHandler extends TextWebSocketHandler {
	private final AppUpdateService appUpdateService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		long railSystemId = (long) session.getAttributes().get("railSystemId");
		appUpdateService.registerSession(railSystemId, session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		// Don't do anything with messages from the web app. At least not yet.
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		appUpdateService.deregisterSession(session);
	}
}
