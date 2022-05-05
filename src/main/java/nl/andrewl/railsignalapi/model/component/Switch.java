package nl.andrewl.railsignalapi.model.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

/**
 * A switch is a component that directs traffic between several connected
 * segments.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Switch extends PathNode {
	/**
	 * The set of all possible configurations that this switch can be in.
	 */
	@OneToMany(mappedBy = "switchComponent", orphanRemoval = true, cascade = CascadeType.ALL)
	private Set<SwitchConfiguration> possibleConfigurations;

	/**
	 * The switch configuration that this switch is currently in.
	 */
	@OneToOne(optional = false, fetch = FetchType.LAZY)
	private SwitchConfiguration activeConfiguration;
}
