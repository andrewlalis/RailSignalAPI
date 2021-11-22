package nl.andrewl.railsignalapi.websocket;

public record SignalUpdateMessage(
		long signalId,
		long fromBranchId,
		long toBranchId,
		String type
) {}
