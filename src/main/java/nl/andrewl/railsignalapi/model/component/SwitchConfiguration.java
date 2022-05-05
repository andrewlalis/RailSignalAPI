package nl.andrewl.railsignalapi.model.component;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

/**
 * A possible connection that can be made between path nodes, if this is set
 * as an active configuration in the linked switch component.
 */
@Entity
@NoArgsConstructor
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
}
