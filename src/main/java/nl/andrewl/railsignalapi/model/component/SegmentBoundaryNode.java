package nl.andrewl.railsignalapi.model.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.andrewl.railsignalapi.model.RailSystem;
import nl.andrewl.railsignalapi.model.Segment;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

/**
 * Component that relays information about trains traversing from one segment
 * to another. It links exactly two segments together at a specific point.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SegmentBoundaryNode extends PathNode {
	/**
	 * The set of segments that this boundary node connects. This should
	 * generally always have exactly two segments. It can never have more than
	 * two segments.
	 */
	@ManyToMany
	private Set<Segment> segments;

	public SegmentBoundaryNode(RailSystem railSystem, Position position, String name, Set<PathNode> connectedNodes, Set<Segment> segments) {
		super(railSystem, position, name, ComponentType.SEGMENT_BOUNDARY, connectedNodes);
		this.segments = segments;
	}
}
