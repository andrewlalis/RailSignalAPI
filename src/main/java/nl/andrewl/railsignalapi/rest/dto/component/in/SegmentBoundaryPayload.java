package nl.andrewl.railsignalapi.rest.dto.component.in;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SegmentBoundaryPayload extends ComponentPayload {
	@NotEmpty @Size(min = 1, max = 2)
	public SegmentPayload[] segments;

	@NotNull @Size(max = 2)
	public NodePayload[] connectedNodes;

	public static class SegmentPayload {
		public long id;
	}

	public static class NodePayload {
		public long id;
	}
}
