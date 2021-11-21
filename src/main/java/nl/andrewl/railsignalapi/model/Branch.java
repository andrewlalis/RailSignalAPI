package nl.andrewl.railsignalapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
public class Branch {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private RailSystem railSystem;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Setter
	private BranchStatus status;

	@OneToMany(mappedBy = "branch", orphanRemoval = true)
	private Set<SignalBranchConnection> signalConnections;

	public Branch(RailSystem railSystem, String name, BranchStatus status) {
		this.railSystem = railSystem;
		this.name = name;
		this.status = status;
		this.signalConnections = new HashSet<>();
	}
}
