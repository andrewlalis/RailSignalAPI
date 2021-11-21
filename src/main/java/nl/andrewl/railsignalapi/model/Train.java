package nl.andrewl.railsignalapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Train {
	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	private Branch currentBranch;
}
