package nl.andrewl.railsignalapi.rest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record LinkTokenPayload(
		@NotNull @NotBlank
		String label,
		@NotEmpty
		long[] componentIds
) {}
