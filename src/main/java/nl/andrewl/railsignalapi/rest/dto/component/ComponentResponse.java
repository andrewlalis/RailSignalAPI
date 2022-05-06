package nl.andrewl.railsignalapi.rest.dto.component;

import nl.andrewl.railsignalapi.model.component.*;

public abstract class ComponentResponse {
	public long id;
	public Position position;
	public String name;
	public String type;
	public boolean online;

	public ComponentResponse(Component c) {
		this.id = c.getId();
		this.position = c.getPosition();
		this.name = c.getName();
		this.type = c.getType().name();
		this.online = c.isOnline();
	}

	public static ComponentResponse of(Component c) {
		return switch (c.getType()) {
			case SIGNAL -> new SignalResponse((Signal) c);
			case SWITCH -> new SwitchResponse((Switch) c);
			case SEGMENT_BOUNDARY -> new SegmentBoundaryNodeResponse((SegmentBoundaryNode) c);
		};
	}
}
