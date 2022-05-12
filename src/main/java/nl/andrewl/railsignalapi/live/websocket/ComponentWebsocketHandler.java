package nl.andrewl.railsignalapi.live.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.dao.ComponentAccessTokenRepository;
import nl.andrewl.railsignalapi.live.ComponentDownlinkService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handler for websocket connections that components open to send and receive
 * real-time updates from the server.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ComponentWebsocketHandler extends TextWebSocketHandler {
	private final ComponentAccessTokenRepository tokenRepository;
	private final ComponentDownlinkService componentDownlinkService;

	@Override
	@Transactional(readOnly = true)
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		long tokenId = (long) session.getAttributes().get("tokenId");
		var token = tokenRepository.findById(tokenId).orElseThrow();
		Set<Long> componentIds = token.getComponents().stream()
				.map(nl.andrewl.railsignalapi.model.component.Component::getId)
				.collect(Collectors.toSet());
		componentDownlinkService.registerDownlink(new WebsocketDownlink(tokenId, session), componentIds);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//		var msg = mapper.readValue(message.getPayload(), SignalUpdateMessage.class);
		//signalService.handleSignalUpdate(msg);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		componentDownlinkService.deregisterDownlink((long) session.getAttributes().get("tokenId"));
	}
}
