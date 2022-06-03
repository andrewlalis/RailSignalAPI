package nl.andrewl.railsignalapi.rest.dto.component.in;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A switch has a set of possible configurations, each of which links two
 * different nodes. Most conventional switches have two configurations, but for
 * universal compatibility, as many as 10 may be allowed.
 */
public class SwitchPayload extends ComponentPayload {
	@NotNull @Size(max = 10)
	public SwitchConfigurationPayload[] possibleConfigurations;

	public static class SwitchConfigurationPayload {
		@NotEmpty @Size(min = 2, max = 2)
		public NodePayload[] nodes;

		public static class NodePayload {
			public long id;
		}
	}
}
