package nl.andrewl.railsignalapi.rest.dto.component;

import nl.andrewl.railsignalapi.model.component.Signal;
import nl.andrewl.railsignalapi.rest.dto.SegmentResponse;

public class SignalResponse extends ComponentResponse {
	public SegmentResponse segment;
	public SignalResponse(Signal s) {
		super(s);
		this.segment = new SegmentResponse(s.getSegment());
	}
}
