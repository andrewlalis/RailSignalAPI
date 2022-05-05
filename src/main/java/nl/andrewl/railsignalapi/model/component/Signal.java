package nl.andrewl.railsignalapi.model.component;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.andrewl.railsignalapi.model.Direction;
import nl.andrewl.railsignalapi.model.RailSystem;
import nl.andrewl.railsignalapi.model.Segment;

import javax.persistence.*;

/**
 * A signal is a component that relays the status of a connected segment to
 * some sort of in-world representation, whether that be a certain light
 * color, or electrical signal.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Signal extends Component {
	/**
	 * The direction this signal is facing. This is the direction in which the
	 * signal connects to a branch.
	 * <pre>
	 *     |-segment A-|-segment B-|
	 *     ===================== <- Rail
	 *                ]+  --->      Signal is facing East, and shows status on
	 *                              its western side. It is connected to segment B.
	 * </pre>
	 */
	@Enumerated(EnumType.STRING)
	private Direction direction;

	/**
	 * The segment that this signal connects to.
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Segment segment;

	public Signal(RailSystem railSystem, Position position, String name, Segment segment, Direction direction) {
		super(railSystem, position, name);
		this.segment = segment;
		this.direction = direction;
	}
}
