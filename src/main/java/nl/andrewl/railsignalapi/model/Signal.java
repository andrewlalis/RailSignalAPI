package nl.andrewl.railsignalapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

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

	public Signal(RailSystem railSystem, String name, Set<SignalBranchConnection> branchConnections) {
		this.railSystem = railSystem;
		this.name = name;
		this.branchConnections = branchConnections;
	}
}
