package nl.andrewl.railsignalapi.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.andrewl.railsignalapi.model.component.Component;

import javax.persistence.*;
import java.util.Set;

/**
 * A secure token that allows users to connect to up- and down-link sockets
 * of components. This token is passed as either a header or query param when
 * establishing a websocket connection, or as part of the connection init
 * packet for plain socket connections.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ComponentAccessToken {
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * The rail system that this token belongs to.
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private RailSystem railSystem;

	/**
	 * A semantic label for this token.
	 */
	@Column(length = 63, unique = true)
	private String label;

	/**
	 * A short prefix of the token, which is useful for speeding up lookup.
	 */
	@Column(nullable = false, length = 7)
	private String tokenPrefix;

	/**
	 * A salted, hashed version of the full token string.
	 */
	@Column(nullable = false, unique = true)
	private String tokenHash;

	/**
	 * The set of components that this access token grants access to.
	 */
	@ManyToMany
	private Set<Component> components;

	public ComponentAccessToken(RailSystem railSystem, String label, String tokenPrefix, String tokenHash, Set<Component> components) {
		this.railSystem = railSystem;
		this.label = label;
		this.tokenPrefix = tokenPrefix;
		this.tokenHash = tokenHash;
		this.components = components;
	}
}
