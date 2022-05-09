package nl.andrewl.railsignalapi.rest.dto.component.in;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LabelPayload extends ComponentPayload {
	@NotNull @NotBlank
	public String text;
}
