package nl.andrewl.railsignalapi.rest.dto.component.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.andrewl.railsignalapi.model.component.Label;
import nl.andrewl.railsignalapi.model.component.Position;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = SignalPayload.class, name = "SIGNAL"),
		@JsonSubTypes.Type(value = SwitchPayload.class, name = "SWITCH"),
		@JsonSubTypes.Type(value = SegmentBoundaryPayload.class, name = "SEGMENT_BOUNDARY"),
		@JsonSubTypes.Type(value = Label.class, name = "LABEL")
})
public abstract class ComponentPayload {
	@NotNull @NotBlank
	public String name;
	@NotNull @NotBlank
	public String type;
	@NotNull
	public Position position;
}
