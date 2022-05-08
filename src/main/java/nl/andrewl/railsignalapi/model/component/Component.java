package nl.andrewl.railsignalapi.model.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.andrewl.railsignalapi.model.RailSystem;

import javax.persistence.*;

/**
 * Represents a physical component of the rail system that the API can interact
 * with, and send or receive data from. For example, a signal, switch, or
 * detector.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Component {
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * The rail system that this component belongs to.
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private RailSystem railSystem;

	/**
	 * The position of this component in the system.
	 */
	@Embedded
	private Position position;

	/**
	 * A human-readable name for the component. This must be unique among all
	 * components in the rail system.
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * The type of this component.
	 */
	@Enumerated(EnumType.ORDINAL)
	private ComponentType type;

	/**
	 * Whether this component is online, meaning that an in-world device is
	 * currently connected to relay information regarding this component.
	 */
	@Column(nullable = false)
	@Setter
	private boolean online = false;

	public Component(RailSystem railSystem, Position position, String name, ComponentType type) {
		this.railSystem = railSystem;
		this.position = position;
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		return o instanceof Component c && this.id != null && this.id.equals(c.id);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		if (name != null) sb.append('[').append(name).append(']');
		sb.append(String.format("@[x=%.1f,y=%.1f,z=%.1f]", position.getX(), position.getY(), position.getZ()));
		return sb.toString();
	}
}
