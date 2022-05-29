package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.live.ComponentDownlinkService;
import nl.andrewl.railsignalapi.live.dto.ComponentDataMessage;
import nl.andrewl.railsignalapi.live.dto.ErrorMessage;
import nl.andrewl.railsignalapi.live.dto.SwitchUpdateMessage;
import nl.andrewl.railsignalapi.live.websocket.AppUpdateService;
import nl.andrewl.railsignalapi.model.component.Switch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
		Optional<Switch> optionalSwitch = switchRepository.findById(msg.cId);
		if (optionalSwitch.isPresent()) {
			Switch sw = optionalSwitch.get();
			for (var config : sw.getPossibleConfigurations()) {
				if (config.getId().equals(msg.activeConfigId)) {
					sw.setActiveConfiguration(config);
					switchRepository.save(sw);
					appUpdateService.sendUpdate(sw.getRailSystem().getId(), new ComponentDataMessage(sw));
					return;
				}
			}
			downlinkService.sendMessage(new ErrorMessage(msg.cId, "Invalid config id."));
		} else {
			downlinkService.sendMessage(new ErrorMessage(msg.cId, "Unknown switch."));
		}
	}
}
