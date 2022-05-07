package nl.andrewl.railsignalapi.rest.dto.component.out;

import nl.andrewl.railsignalapi.model.component.SegmentBoundaryNode;
import nl.andrewl.railsignalapi.rest.dto.SegmentResponse;

import java.util.List;

public class SegmentBoundaryNodeResponse extends PathNodeResponse {
	public List<SegmentResponse> segments;

	public SegmentBoundaryNodeResponse(SegmentBoundaryNode n) {
		super(n);
		this.segments = n.getSegments().stream().map(SegmentResponse::new).toList();
	}
}
