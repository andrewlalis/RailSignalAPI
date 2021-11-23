package nl.andrewl.railsignalapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Signal {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private RailSystem railSystem;

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "signal", orphanRemoval = true, cascade = CascadeType.ALL)
	private Set<SignalBranchConnection> branchConnections;

	@Embedded
	private Position position;

	@Column(nullable = false)
	@Setter
	private boolean online = false;

	public Signal(RailSystem railSystem, String name, Position position, Set<SignalBranchConnection> branchConnections) {
		this.railSystem = railSystem;
		this.name = name;
		this.position = position;
		this.branchConnections = branchConnections;
	}
}
