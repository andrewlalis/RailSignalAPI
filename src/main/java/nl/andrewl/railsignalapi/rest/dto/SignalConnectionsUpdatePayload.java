package nl.andrewl.railsignalapi.rest.dto;

import java.util.List;

public record SignalConnectionsUpdatePayload(
		List<ConnectionData> connections
) {
	public static record ConnectionData(long from, long to) {}
}
