package nl.andrewl.railsignalapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents a closed system that contains a collection of components.
 */
@Entity
@Getter
@NoArgsConstructor
public class RailSystem {
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * The name of this system.
	 */
	@Column(nullable = false, unique = true)
	private String name;

	public RailSystem(String name) {
		this.name = name;
	}
}
