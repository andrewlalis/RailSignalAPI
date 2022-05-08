package nl.andrewl.railsignalapi.model.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * A three-dimensional position for a component within a system.
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Position {
	private double x;
	private double y;
	private double z;
}
