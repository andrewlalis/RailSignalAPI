package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.live.ComponentDownlinkService;
import nl.andrewl.railsignalapi.live.dto.SwitchUpdateMessage;
import nl.andrewl.railsignalapi.live.websocket.AppUpdateService;
import nl.andrewl.railsignalapi.model.component.Switch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing switches.
 */
@Service
@RequiredArgsConstructor
public class SwitchService {
	private final ComponentRepository<Switch> switchRepository;

	private final ComponentDownlinkService downlinkService;
	private final AppUpdateService appUpdateService;

	@Transactional
	public void onSwitchUpdate(SwitchUpdateMessage msg) {
		switchRepository.findById(msg.cId).ifPresent(sw -> {
			sw.findConfiguration(msg.configuration).ifPresent(config -> {
				sw.setActiveConfiguration(config);
				switchRepository.save(sw);
				appUpdateService.sendComponentUpdate(sw.getRailSystem().getId(), sw.getId());
			});
		});
	}
}
