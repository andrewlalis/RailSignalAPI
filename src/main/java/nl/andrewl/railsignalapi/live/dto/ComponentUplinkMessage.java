package nl.andrewl.railsignalapi.live.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The parent class for all uplink messages that can be sent by connected
 * components.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = SegmentBoundaryUpdateMessage.class, name = "sb"),
		@JsonSubTypes.Type(value = SwitchUpdateMessage.class, name = "sw")
})
public abstract class ComponentUplinkMessage {
	public long cId;
	public String type;
}
