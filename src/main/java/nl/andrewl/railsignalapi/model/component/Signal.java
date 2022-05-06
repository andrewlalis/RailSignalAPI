package nl.andrewl.railsignalapi.model.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.andrewl.railsignalapi.model.RailSystem;
import nl.andrewl.railsignalapi.model.Segment;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * A signal is a component that relays the status of a connected segment to
 * some sort of in-world representation, whether that be a certain light
 * color, or electrical signal.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Signal extends Component {
	/**
	 * The segment that this signal connects to.
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Segment segment;

	public Signal(RailSystem railSystem, Position position, String name, Segment segment) {
		super(railSystem, position, name, ComponentType.SIGNAL);
		this.segment = segment;
	}
}
