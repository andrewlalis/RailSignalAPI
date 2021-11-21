package nl.andrewl.railsignalapi.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.service.SignalService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignalWebSocketHandler extends TextWebSocketHandler {
	private final ObjectMapper mapper = new ObjectMapper();
	private final SignalService signalService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String signalIdHeader = session.getHandshakeHeaders().getFirst("X-RailSignal-SignalId");
		if (signalIdHeader == null || signalIdHeader.isBlank()) {
			session.close(CloseStatus.PROTOCOL_ERROR);
			return;
		}
		long signalId = Long.parseLong(signalIdHeader);
		session.getAttributes().put("signalId", signalId);
		signalService.registerSignalWebSocketSession(signalId, session);
		log.info("Connection established with signal {}.", signalId);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		var msg = mapper.readValue(message.getPayload(), SignalUpdateMessage.class);
		Long signalId = (Long) session.getAttributes().get("signalId");
		if (signalId == null) {
			log.warn("Got text message from a websocket session that did not establish a signalId session attribute.");
		} else {
			log.info("Received update from signal {}.", signalId);
			signalService.handleSignalUpdate(signalId, msg);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		signalService.deregisterSignalWebSocketSession((Long) session.getAttributes().get("signalId"));
		log.info("Closed connection to signal {}. Status: {}", session.getAttributes().get("signalId"), status.toString());
	}
}
