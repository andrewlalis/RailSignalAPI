package nl.andrewl.railsignalapi.websocket;

public record SignalUpdateMessage(
		long fromBranchId,
		long toBranchId,
		String type
) {}
