package nl.andrewl.railsignalapi.rest.dto;

import nl.andrewl.railsignalapi.model.Segment;

public class SegmentResponse {
	public long id;
	public String name;
	public boolean occupied;

	public SegmentResponse(Segment s) {
		this.id = s.getId();
		this.name = s.getName();
		this.occupied = s.isOccupied();
	}
}
