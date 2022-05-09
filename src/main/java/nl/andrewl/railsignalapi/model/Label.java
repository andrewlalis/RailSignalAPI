package nl.andrewl.railsignalapi.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.model.component.ComponentType;
import nl.andrewl.railsignalapi.model.component.Position;

import javax.persistence.*;

/**
 * A simple label element that allows text to be placed in the rail system
 * model.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Label extends Component {
	@Column(nullable = false)
	private String text;

	public Label(RailSystem rs, Position position, String name, String text) {
		super(rs, position, name, ComponentType.LABEL);
		this.text = text;
	}
}
