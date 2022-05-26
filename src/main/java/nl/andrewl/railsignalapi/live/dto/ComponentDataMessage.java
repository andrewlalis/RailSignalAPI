package nl.andrewl.railsignalapi.live.dto;

import lombok.Getter;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.rest.dto.component.out.ComponentResponse;

/**
 * A message that's sent to devices which contains the full component response
 * for a specific component.
 */
@Getter
public class ComponentDataMessage extends ComponentMessage {
	private final ComponentResponse data;

	public ComponentDataMessage(Component c) {
		super(c.getId(), "COMPONENT_DATA");
		this.data = ComponentResponse.of(c);
	}
}
