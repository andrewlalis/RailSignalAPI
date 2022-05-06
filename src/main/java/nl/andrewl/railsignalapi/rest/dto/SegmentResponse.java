package nl.andrewl.railsignalapi.rest.dto;

import nl.andrewl.railsignalapi.model.Segment;

public class SegmentResponse {
	public long id;
	public String name;

	public SegmentResponse(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public SegmentResponse(Segment s) {
		this(s.getId(), s.getName());
	}
}
