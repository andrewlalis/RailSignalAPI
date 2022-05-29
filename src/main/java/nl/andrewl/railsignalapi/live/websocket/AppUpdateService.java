package nl.andrewl.railsignalapi.live.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.live.dto.ComponentDataMessage;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.util.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A service that can be used to send live updates of a rail system's state
 * to connected front-end web apps.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppUpdateService {
	private final Map<Long, Set<WebSocketSession>> sessions = new HashMap<>();

	private final ComponentRepository<Component> componentRepository;

	public synchronized void registerSession(long rsId, WebSocketSession session) {
		Set<WebSocketSession> sessionsForRs = sessions.computeIfAbsent(rsId, x -> new HashSet<>());
		sessionsForRs.add(session);
		log.info("Registered a new app websocket session for rail system id " + rsId);
	}

	public synchronized void deregisterSession(WebSocketSession session) {
		Set<Long> orphans = new HashSet<>();
		// Remove the session from any rail systems it's subscribed to.
		for (var entry : sessions.entrySet()) {
			if (entry.getValue().contains(session)) {
				entry.getValue().remove(session);
				if (entry.getValue().isEmpty()) {
					orphans.add(entry.getKey());
				}
			}
		}
		// Clean up the sessions map by removing any rail systems for which there are no subscriptions.
		for (var orphanRsId : orphans) {
			sessions.remove(orphanRsId);
		}
		log.info("De-registered an app websocket session.");
	}

	/**
	 * Sends an update to any connected apps.
	 * @param rsId The id of the rail system that the update pertains to.
	 * @param msg The message to send.
	 */
	public synchronized void sendUpdate(long rsId, Object msg) {
		Set<WebSocketSession> sessionsForRs = sessions.get(rsId);
		if (sessionsForRs != null) {
			try {
				String json = JsonUtils.toJson(msg);
				for (var session : sessionsForRs) {
					try {
						session.sendMessage(new TextMessage(json));
					} catch (IOException e) {
						log.warn("An error occurred when sending message to websocket session.", e);
					}
				}
			} catch (IOException e) {
				log.error("Failed to produce JSON for message update for apps.", e);
			}

		}
	}

	public void sendComponentUpdate(long rsId, long componentId) {
		componentRepository.findByIdAndRailSystemId(componentId, rsId).ifPresent(component -> {
			sendUpdate(rsId, new ComponentDataMessage(component));
		});
	}
}
