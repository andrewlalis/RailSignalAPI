package nl.andrewl.railsignalapi.rest.dto.component.in;

import javax.validation.constraints.NotNull;

public class SignalPayload extends ComponentPayload {
	@NotNull
	public SegmentPayload segment;
	public static class SegmentPayload {
		public long id;
	}
}
