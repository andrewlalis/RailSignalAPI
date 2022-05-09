package nl.andrewl.railsignalapi.rest.dto.component.in;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class SegmentBoundaryPayload extends ComponentPayload {
	@NotEmpty @Size(min = 1, max = 2)
	public SegmentPayload[] segments;

	public static class SegmentPayload {
		public long id;
	}
}
