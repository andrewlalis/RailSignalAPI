package nl.andrewl.railsignalapi.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents a closed system that contains a collection of components.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RailSystem {
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * The name of this system.
	 */
	@Column(nullable = false, unique = true)
	private String name;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@PrimaryKeyJoinColumn
	@Setter
	private RailSystemSettings settings;

	public RailSystem(String name) {
		this.name = name;
	}
}
