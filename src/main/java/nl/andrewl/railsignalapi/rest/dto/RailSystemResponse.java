package nl.andrewl.railsignalapi.rest.dto;

import nl.andrewl.railsignalapi.model.RailSystem;

public record RailSystemResponse(long id, String name) {
	public RailSystemResponse(RailSystem rs) {
		this(rs.getId(), rs.getName());
	}
}
