package nl.andrewl.railsignalapi.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignalWebSocketHandler extends TextWebSocketHandler {
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String signalIdHeader = session.getHandshakeHeaders().getFirst("X-RailSignal-SignalId");
		if (signalIdHeader == null || signalIdHeader.isBlank()) {
			session.close(CloseStatus.PROTOCOL_ERROR);
			return;
		}
		Set<Long> ids = new HashSet<>();
		for (var idStr : signalIdHeader.split(",")) {
			ids.add(Long.parseLong(idStr.trim()));
		}
		//signalService.registerSignalWebSocketSession(ids, session);
		log.info("Connection established with signals {}.", ids);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		var msg = mapper.readValue(message.getPayload(), SignalUpdateMessage.class);
		//signalService.handleSignalUpdate(msg);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		//signalService.deregisterSignalWebSocketSession(session);
		log.info("Closed connection {}. Status: {}", session.getId(), status.toString());
	}
}
