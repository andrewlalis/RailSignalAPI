package nl.andrewl.railsignalapi.model.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * A three-dimensional position for a component within a system.
 */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Position {
	@NotNull
	private double x;
	@NotNull
	private double y;
	@NotNull
	private double z;
}
