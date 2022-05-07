package nl.andrewl.railsignalapi.rest.dto.component.out;

import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.model.component.Position;

public record SimpleComponentResponse (
		long id,
		Position position,
		String name,
		String type,
		boolean online
) {
	public SimpleComponentResponse(Component c) {
		this(
				c.getId(),
				c.getPosition(),
				c.getName(),
				c.getType().name(),
				c.isOnline()
		);
	}
}
