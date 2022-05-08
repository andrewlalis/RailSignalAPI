package nl.andrewl.railsignalapi.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * A simple label element that allows text to be placed in the rail system
 * model.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Label {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private RailSystem railSystem;

	@Column(nullable = false, length = 63)
	private String text;

	public Label(RailSystem rs, String text) {
		this.railSystem = rs;
		this.text = text;
	}
}
