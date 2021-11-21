package nl.andrewl.railsignalapi.rest.dto;

import java.util.List;

public record SignalCreationPayload(
		String name,
		List<BranchData> branchConnections
) {
	public static record BranchData(String direction, String name, Long id) {}
}
