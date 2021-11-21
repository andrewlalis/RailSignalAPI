package nl.andrewl.railsignalapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class SignalBranchConnection {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false)
	private Signal signal;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	private Branch branch;

	@Enumerated(EnumType.STRING)
	private Direction direction;

	public SignalBranchConnection(Signal signal, Branch branch, Direction direction) {
		this.signal = signal;
		this.branch = branch;
		this.direction = direction;
	}
}