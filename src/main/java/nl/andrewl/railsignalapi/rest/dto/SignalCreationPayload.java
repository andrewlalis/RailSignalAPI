package nl.andrewl.railsignalapi.rest.dto;

import nl.andrewl.railsignalapi.model.Position;

import java.util.List;

public record SignalCreationPayload(
		String name,
		Position position,
		List<BranchData> branchConnections
) {
	public static record BranchData(String direction, String name, Long id) {}
}
