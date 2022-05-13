package nl.andrewl.railsignalapi.live.tcp_socket;

public record ConnectMessage(
		boolean valid,
		String message
) {}
