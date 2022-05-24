package nl.andrewl.railsignalapi.rest.dto;

import nl.andrewl.railsignalapi.model.RailSystemSettings;

import javax.validation.constraints.NotNull;

public record RailSystemSettingsPayload(
	@NotNull
	boolean readOnly,

	@NotNull
	boolean authenticationRequired
) {
	public RailSystemSettingsPayload(RailSystemSettings settings) {
		this(
				settings.isReadOnly(),
				settings.isAuthenticationRequired()
		);
	}
}
