package nl.andrewl.railsignalapi.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * A single entity that's paired with each rail system, and contains some
 * settings that apply globally to the rail system.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RailSystemSettings {
	@Id
	@Column(name = "rail_system_id", unique = true, nullable = false)
	private Long id;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "rail_system_id")
	private RailSystem railSystem;

	/**
	 * Tells whether the rail system is read-only, meaning it cannot be changed
	 * by any means.
	 */
	@Column(nullable = false) @Setter
	private boolean readOnly;

	/**
	 * Whether it is required for users to be authenticated in order to access
	 * this rail system. If true, the system will check that users are
	 * authenticated prior to any access or modification of the system.
	 */
	@Column(nullable = false) @Setter
	private boolean authenticationRequired;

	public RailSystemSettings(RailSystem rs) {
		this.id = rs.getId();
		this.railSystem = rs;
		this.readOnly = false;
		this.authenticationRequired = false;
	}
}
