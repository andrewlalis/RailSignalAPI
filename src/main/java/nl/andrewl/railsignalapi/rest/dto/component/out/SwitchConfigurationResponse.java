package nl.andrewl.railsignalapi.rest.dto.component.out;

import nl.andrewl.railsignalapi.model.component.SwitchConfiguration;

import java.util.Comparator;
import java.util.List;

public record SwitchConfigurationResponse (
		long id,
		List<SimpleComponentResponse> nodes
) {
	public SwitchConfigurationResponse(SwitchConfiguration sc) {
		this(
				sc.getId(),
				sc.getNodes().stream()
						.map(SimpleComponentResponse::new)
						.sorted(Comparator.comparing(SimpleComponentResponse::name))
						.toList()
		);
	}
}
