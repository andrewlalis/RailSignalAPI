package nl.andrewl.railsignalapi.model.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.andrewl.railsignalapi.model.RailSystem;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * A simple label element that allows text to be placed in the rail system
 * model.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Label extends Component {
	@Column(nullable = false)
	@Setter
	private String text;

	public Label(RailSystem rs, Position position, String name, String text) {
		super(rs, position, name, ComponentType.LABEL);
		this.text = text;
	}
}
