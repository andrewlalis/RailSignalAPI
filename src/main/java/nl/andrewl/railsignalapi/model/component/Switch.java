package nl.andrewl.railsignalapi.model.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.andrewl.railsignalapi.model.RailSystem;

import javax.persistence.*;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
	 * The switch configuration that this switch is currently in. If null, then
	 * we don't know what configuration the switch is in.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@Setter
	private SwitchConfiguration activeConfiguration;

	public Switch(RailSystem railSystem, Position position, String name, Set<PathNode> connectedNodes, Set<SwitchConfiguration> possibleConfigurations, SwitchConfiguration activeConfiguration) {
		super(railSystem, position, name, ComponentType.SWITCH, connectedNodes);
		this.possibleConfigurations = possibleConfigurations;
		this.activeConfiguration = activeConfiguration;
	}

	public Optional<SwitchConfiguration> findConfiguration(Set<Long> pathNodeIds) {
		for (var config : possibleConfigurations) {
			Set<Long> configNodeIds = config.getNodes().stream().map(Component::getId).collect(Collectors.toSet());
			if (pathNodeIds.equals(configNodeIds)) return Optional.of(config);
		}
		return Optional.empty();
	}
}
