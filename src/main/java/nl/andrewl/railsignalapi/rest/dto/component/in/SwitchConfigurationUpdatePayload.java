package nl.andrewl.railsignalapi.rest.dto.component.in;

import javax.validation.constraints.NotNull;

public record SwitchConfigurationUpdatePayload(
		@NotNull
		long activeConfigurationId
) {}
