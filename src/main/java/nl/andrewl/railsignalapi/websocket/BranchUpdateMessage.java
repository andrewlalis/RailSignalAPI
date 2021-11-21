package nl.andrewl.railsignalapi.websocket;

public record BranchUpdateMessage(long branchId, String status) {
}
