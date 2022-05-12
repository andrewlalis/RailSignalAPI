package nl.andrewl.railsignalapi.live;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A service that manages all the active component downlink connections.
 */
@Service
@RequiredArgsConstructor
public class ComponentDownlinkService {
	private final Map<ComponentDownlink, Set<Long>> componentDownlinks = new HashMap<>();

	public synchronized void registerDownlink(ComponentDownlink downlink, Set<Long> componentIds) {
		componentDownlinks.put(downlink, componentIds);
	}

	public synchronized void deregisterDownlink(ComponentDownlink downlink) {
		componentDownlinks.remove(downlink);
	}

	public synchronized void deregisterDownlink(long tokenId) {
		List<ComponentDownlink> removeSet = componentDownlinks.keySet().stream()
				.filter(downlink -> downlink.getId() == tokenId).toList();
		for (var downlink : removeSet) {
			componentDownlinks.remove(downlink);
		}
	}
}
