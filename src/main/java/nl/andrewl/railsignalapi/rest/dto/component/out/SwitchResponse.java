package nl.andrewl.railsignalapi.rest.dto.component.out;

import nl.andrewl.railsignalapi.model.component.Switch;

import java.util.Comparator;
import java.util.List;

public class SwitchResponse extends PathNodeResponse {
	public List<SwitchConfigurationResponse> possibleConfigurations;
	public SwitchConfigurationResponse activeConfiguration;

	public SwitchResponse(Switch s) {
		super(s);
		this.possibleConfigurations = s.getPossibleConfigurations().stream()
				.map(SwitchConfigurationResponse::new)
				.sorted(Comparator.comparing(SwitchConfigurationResponse::id))
				.toList();
		this.activeConfiguration = s.getActiveConfiguration() == null ? null : new SwitchConfigurationResponse(s.getActiveConfiguration());
	}
}
