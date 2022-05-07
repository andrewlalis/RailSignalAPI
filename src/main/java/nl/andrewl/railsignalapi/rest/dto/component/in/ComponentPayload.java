package nl.andrewl.railsignalapi.rest.dto.component.in;

import nl.andrewl.railsignalapi.model.component.Position;

public abstract class ComponentPayload {
	public String name;
	public String type;
	public Position position;
}
