package nl.andrewl.railsignalapi.live.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NoArgsConstructor;

/**
 * Base class for all messages that will be sent to components.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = SegmentBoundaryUpdateMessage.class, name = "SEGMENT_BOUNDARY_UPDATE"),
		@JsonSubTypes.Type(value = SwitchUpdateMessage.class, name = "SWITCH_UPDATE"),
		@JsonSubTypes.Type(value = ErrorMessage.class, name = "ERROR")
})
@NoArgsConstructor
public abstract class ComponentMessage {
	/**
	 * The id of the component that this message is for.
	 */
	public long cId;

	/**
	 * The type of message.
	 */
	public String type;

	public ComponentMessage(long cId, String type) {
		this.cId = cId;
		this.type = type;
	}
}
