package nl.andrewl.railsignalapi.live;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.LinkTokenRepository;
import nl.andrewl.railsignalapi.live.dto.ComponentDataMessage;
import nl.andrewl.railsignalapi.live.dto.ComponentMessage;
import nl.andrewl.railsignalapi.live.dto.SegmentStatusMessage;
import nl.andrewl.railsignalapi.live.dto.SwitchUpdateMessage;
import nl.andrewl.railsignalapi.live.websocket.AppUpdateService;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.model.component.Signal;
import nl.andrewl.railsignalapi.model.component.Switch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A service that manages all the active component downlink connections.
 *
 * We keep track of active component downlinks by storing a mapping which maps
 * each downlink to the set of components it is responsible for, and another
 * mapping that maps each online component to the set of downlinks that are
 * responsible for it.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComponentDownlinkService {
	private final Map<ComponentDownlink, Set<Long>> componentDownlinks = new HashMap<>();
	private final Map<Long, Set<ComponentDownlink>> downlinksByCId = new HashMap<>();

	private final LinkTokenRepository tokenRepository;
	private final ComponentRepository<Component> componentRepository;
	private final AppUpdateService appUpdateService;

	/**
	 * Registers a new active downlink to one or more components. Sets all
	 * linked components as online, and sends messages to any connected apps
	 * to notify them of the update components.
	 * @param downlink The downlink to register.
	 */
	@Transactional
	public synchronized void registerDownlink(ComponentDownlink downlink) throws Exception {
		Set<Component> components = tokenRepository.findById(downlink.getTokenId()).orElseThrow().getComponents();
		componentDownlinks.put(downlink, components.stream().map(Component::getId).collect(Collectors.toSet()));
		for (var c : components) {
			c.setOnline(true);
			componentRepository.saveAndFlush(c); // Make sure to flush, so that online status is immediately visible everywhere.

			// Immediately send a data message to the downlink and app for each component that comes online.
			var msg = new ComponentDataMessage(c);
			downlink.send(msg);
			appUpdateService.sendUpdate(c.getRailSystem().getId(), msg);

			// Send initial data updates to make sure that devices' state is up to date immediately.
			if (c instanceof Signal sig) {
				downlink.send(new SegmentStatusMessage(c.getId(), sig.getSegment().getId(), sig.getSegment().isOccupied()));
			} else if (c instanceof Switch sw) {
				long activeConfigId;
				if (sw.getActiveConfiguration() != null) {
					activeConfigId = sw.getActiveConfiguration().getId();
				} else {
					activeConfigId = sw.getPossibleConfigurations().stream().findAny().orElseThrow().getId();
				}
				downlink.send(new SwitchUpdateMessage(c.getId(), activeConfigId));
			}

			Set<ComponentDownlink> downlinks = downlinksByCId.computeIfAbsent(c.getId(), aLong -> new HashSet<>());
			downlinks.add(downlink);
		}
		log.info("Registered downlink with token id {}.", downlink.getTokenId());
	}

	/**
	 * De-registers a downlink to components. This should be called when this
	 * downlink is closed.
	 * @param downlink The downlink to de-register.
	 */
	@Transactional
	public synchronized void deregisterDownlink(ComponentDownlink downlink) {
		Set<Long> componentIds = componentDownlinks.remove(downlink);
		if (componentIds != null) {
			for (var cId : componentIds) {
				componentRepository.findById(cId).ifPresent(component -> {
					component.setOnline(false);
					componentRepository.save(component);
					appUpdateService.sendComponentUpdate(component.getRailSystem().getId(), component.getId());
				});
				Set<ComponentDownlink> downlinks = downlinksByCId.get(cId);
				if (downlinks != null) {
					downlinks.remove(downlink);
					if (downlinks.isEmpty()) {
						downlinksByCId.remove(cId);
					}
				}
			}
		}
		try {
			downlink.shutdown();
		} catch (Exception e) {
			log.warn("An error occurred while shutting down a component downlink.", e);
		}
		log.info("De-registered downlink with token id {}.", downlink.getTokenId());
	}

	@Transactional
	public synchronized void deregisterDownlink(long tokenId) {
		List<ComponentDownlink> removeSet = componentDownlinks.keySet().stream()
				.filter(downlink -> downlink.getTokenId() == tokenId).toList();
		for (var downlink : removeSet) {
			deregisterDownlink(downlink);
		}
	}

	public void sendMessage(long componentId, Object msg) {
		var downlinks = downlinksByCId.get(componentId);
		if (downlinks != null) {
			for (var downlink : downlinks) {
				try {
					downlink.send(msg);
				} catch (Exception e) {
					log.warn("An error occurred while sending a message to downlink with token id " + downlink.getTokenId(), e);
				}
			}
		}
	}

	public void sendMessage(ComponentMessage msg) {
		sendMessage(msg.cId, msg);
	}
}
