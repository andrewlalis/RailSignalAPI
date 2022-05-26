package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.live.ComponentDownlinkService;
import nl.andrewl.railsignalapi.live.dto.ErrorMessage;
import nl.andrewl.railsignalapi.live.dto.SwitchUpdateMessage;
import nl.andrewl.railsignalapi.live.websocket.AppUpdateService;
import nl.andrewl.railsignalapi.model.component.Switch;
import nl.andrewl.railsignalapi.model.component.SwitchConfiguration;
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
			sw.getPossibleConfigurations().stream()
				.filter(c -> c.getId().equals(msg.activeConfigId))
				.findFirst()
				.ifPresentOrElse(config -> {
					sw.setActiveConfiguration(config);
					switchRepository.save(sw);
					appUpdateService.sendComponentUpdate(sw.getRailSystem().getId(), sw.getId());
				}, () -> {
					downlinkService.sendMessage(new ErrorMessage(sw.getId(), "Invalid active config id."));
				});
		});
	}
}
