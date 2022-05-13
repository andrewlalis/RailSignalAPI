package nl.andrewl.railsignalapi.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.andrewl.railsignalapi.model.component.SegmentBoundaryNode;
import nl.andrewl.railsignalapi.model.component.Signal;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a traversable segment of a rail system that components can
 * connect to.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Segment {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private RailSystem railSystem;

	/**
	 * A unique name for this segment.
	 */
	@Column
	private String name;

	/**
	 * Whether this segment is occupied by a train.
	 */
	@Column(nullable = false)
	@Setter
	private boolean occupied;

	/**
	 * The signals that are connected to this branch.
	 */
	@OneToMany(mappedBy = "segment")
	private Set<Signal> signals;

	/**
	 * The set of nodes from which trains can enter and exit this segment.
	 */
	@ManyToMany(mappedBy = "segments")
	private Set<SegmentBoundaryNode> boundaryNodes;

	public Segment(RailSystem railSystem, String name) {
		this.railSystem = railSystem;
		this.name = name;
		this.signals = new HashSet<>();
		this.boundaryNodes = new HashSet<>();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		return o instanceof Segment s && this.id != null && this.id.equals(s.id);
	}
}
