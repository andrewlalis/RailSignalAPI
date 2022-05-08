package nl.andrewl.railsignalapi.model.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

/**
 * A possible connection that can be made between path nodes, if this is set
 * as an active configuration in the linked switch component.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SwitchConfiguration {
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * The switch component that this configuration belongs to.
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Switch switchComponent;

	/**
	 * The set of nodes that this switch configuration connects. This should
	 * be almost always a set of two nodes.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<PathNode> nodes;

	public SwitchConfiguration(Switch switchComponent, Set<PathNode> nodes) {
		this.switchComponent = switchComponent;
		this.nodes = nodes;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		return o instanceof SwitchConfiguration sc &&
				sc.switchComponent.equals(this.switchComponent) &&
				sc.nodes.equals(this.nodes);
	}
}
