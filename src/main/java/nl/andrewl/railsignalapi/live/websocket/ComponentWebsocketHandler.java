package nl.andrewl.railsignalapi.live.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.live.ComponentDownlinkService;
import nl.andrewl.railsignalapi.live.ComponentUplinkMessageHandler;
import nl.andrewl.railsignalapi.live.dto.ComponentMessage;
import nl.andrewl.railsignalapi.util.JsonUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * Handler for websocket connections that components open to send and receive
 * real-time updates from the server.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ComponentWebsocketHandler extends TextWebSocketHandler {
	private final ComponentDownlinkService componentDownlinkService;
	private final ComponentUplinkMessageHandler uplinkMessageHandler;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		long tokenId = (long) session.getAttributes().get("tokenId");
		try {
			componentDownlinkService.registerDownlink(new WebsocketDownlink(tokenId, session));
		} catch (Exception e) {
			log.error("An error occurred while registering a new websocket downlink.", e);
			try {
				session.close();
			} catch (IOException ex) {
				log.error("An error occurred while closing a websocket downlink.", e);
			}
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		var msg = JsonUtils.readMessage(message.getPayload(), ComponentMessage.class);
		uplinkMessageHandler.messageReceived(msg);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		componentDownlinkService.deregisterDownlink((long) session.getAttributes().get("tokenId"));
	}
}
