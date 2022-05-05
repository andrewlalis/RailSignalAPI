package nl.andrewl.railsignalapi.model.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.andrewl.railsignalapi.model.RailSystem;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import java.util.Set;

/**
 * A node that, together with other nodes, forms a path that trains can follow
 * to traverse through segments in the rail system.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PathNode extends Component {
	/**
	 * The set of nodes that this one is connected to.
	 */
	@ManyToMany
	private Set<PathNode> connectedNodes;

	public PathNode(RailSystem railSystem, Position position, String name, Set<PathNode> connectedNodes) {
		super(railSystem, position, name);
		this.connectedNodes = connectedNodes;
	}
}
