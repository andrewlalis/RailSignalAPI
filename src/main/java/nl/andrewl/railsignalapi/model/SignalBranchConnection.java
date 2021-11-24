package nl.andrewl.railsignalapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
public class SignalBranchConnection implements Comparable<SignalBranchConnection> {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false)
	private Signal signal;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	private Branch branch;

	@Enumerated(EnumType.STRING)
	private Direction direction;

	@ManyToMany
	private Set<SignalBranchConnection> reachableSignalConnections;

	public SignalBranchConnection(Signal signal, Branch branch, Direction direction) {
		this.signal = signal;
		this.branch = branch;
		this.direction = direction;
		reachableSignalConnections = new HashSet<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		return this.id != null &&
				o instanceof SignalBranchConnection sbc && sbc.getId() != null &&
				this.id.equals(sbc.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public int compareTo(SignalBranchConnection o) {
		int c = Long.compare(this.getSignal().getId(), o.getSignal().getId());
		if (c != 0) return c;
		return this.direction.compareTo(o.getDirection());
	}
}
