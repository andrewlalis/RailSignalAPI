package nl.andrewl.railsignalapi.rest.dto;

import nl.andrewl.railsignalapi.model.Segment;
import nl.andrewl.railsignalapi.rest.dto.component.out.SegmentBoundaryNodeResponse;
import nl.andrewl.railsignalapi.rest.dto.component.out.SignalResponse;

import java.util.List;

public class FullSegmentResponse extends SegmentResponse {
	public List<SignalResponse> signals;
	public List<SegmentBoundaryNodeResponse> boundaryNodes;

	public FullSegmentResponse(Segment s) {
		super(s);
		this.signals = s.getSignals().stream().map(SignalResponse::new).toList();
		this.boundaryNodes = s.getBoundaryNodes().stream().map(SegmentBoundaryNodeResponse::new).toList();
	}
}
