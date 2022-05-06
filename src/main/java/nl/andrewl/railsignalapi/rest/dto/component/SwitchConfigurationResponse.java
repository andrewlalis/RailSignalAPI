package nl.andrewl.railsignalapi.rest.dto.component;

import nl.andrewl.railsignalapi.model.component.SwitchConfiguration;

import java.util.List;

public record SwitchConfigurationResponse (
		long id,
		List<SimpleComponentResponse> nodes
) {
	public SwitchConfigurationResponse(SwitchConfiguration sc) {
		this(
				sc.getId(),
				sc.getNodes().stream().map(SimpleComponentResponse::new).toList()
		);
	}
}
