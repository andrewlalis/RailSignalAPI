package nl.andrewl.railsignalapi.live.websocket;

import nl.andrewl.railsignalapi.live.ComponentDownlink;
import nl.andrewl.railsignalapi.util.JsonUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebsocketDownlink extends ComponentDownlink {
	private final WebSocketSession webSocketSession;

	public WebsocketDownlink(long id, WebSocketSession webSocketSession) {
		super(id);
		this.webSocketSession = webSocketSession;
	}

	@Override
	public void send(Object msg) throws Exception {
		webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(msg)));
	}

	@Override
	public void shutdown() throws Exception {
		webSocketSession.close();
	}
}
