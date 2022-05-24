package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.model.RailSystem;
import nl.andrewl.railsignalapi.model.RailSystemSettings;
import nl.andrewl.railsignalapi.rest.dto.RailSystemSettingsPayload;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RailSystemSettingsService {
	private final RailSystemRepository railSystemRepository;

	@Transactional
	public RailSystemSettingsPayload getSettings(long rsId) {
		var rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return new RailSystemSettingsPayload(getOrCreateSettings(rs));
	}

	@Transactional
	public RailSystemSettingsPayload setSettings(long rsId, RailSystemSettingsPayload payload) {
		var rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		RailSystemSettings settings = getOrCreateSettings(rs);
		settings.setReadOnly(payload.readOnly());
		settings.setAuthenticationRequired(payload.authenticationRequired());
		railSystemRepository.save(rs);
		return new RailSystemSettingsPayload(settings);
	}

	private RailSystemSettings getOrCreateSettings(RailSystem rs) {
		if (rs.getSettings() == null) {
			rs.setSettings(new RailSystemSettings(rs));
			railSystemRepository.save(rs);
		}
		return rs.getSettings();
	}
}
