package nl.andrewl.railsignalapi.rest.dto.component.out;

import nl.andrewl.railsignalapi.model.component.Label;
import nl.andrewl.railsignalapi.model.component.*;

/**
 * The base class for any component's API response object.
 */
public abstract class ComponentResponse {
	public long id;
	public Position position;
	public String name;
	public String type;
	public Boolean online;

	public ComponentResponse(Component c) {
		this.id = c.getId();
		this.position = c.getPosition();
		this.name = c.getName();
		this.type = c.getType().name();
		this.online = c.getOnline();
	}

	/**
	 * Builds a full response for a component of any type.
	 * @param c The component to build a response for.
	 * @return The response.
	 */
	public static ComponentResponse of(Component c) {
		return switch (c.getType()) {
			case SIGNAL -> new SignalResponse((Signal) c);
			case SWITCH -> new SwitchResponse((Switch) c);
			case SEGMENT_BOUNDARY -> new SegmentBoundaryNodeResponse((SegmentBoundaryNode) c);
			case LABEL -> new LabelResponse((Label) c);
		};
	}
}
