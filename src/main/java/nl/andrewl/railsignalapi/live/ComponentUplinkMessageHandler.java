package nl.andrewl.railsignalapi.live;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.live.dto.ComponentMessage;
import nl.andrewl.railsignalapi.live.dto.SegmentBoundaryUpdateMessage;
import nl.andrewl.railsignalapi.live.dto.SwitchUpdateMessage;
import nl.andrewl.railsignalapi.service.SegmentService;
import nl.andrewl.railsignalapi.service.SwitchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A central service that manages all incoming component messages from any
 * connected component links.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComponentUplinkMessageHandler {
	private final SwitchService switchService;
	private final SegmentService segmentService;

	@Transactional
	public void messageReceived(ComponentMessage msg) {
		if (msg instanceof SegmentBoundaryUpdateMessage sb) {
			segmentService.onBoundaryUpdate(sb);
		} else if (msg instanceof SwitchUpdateMessage sw) {
			switchService.onSwitchUpdate(sw);
		}
	}
}
