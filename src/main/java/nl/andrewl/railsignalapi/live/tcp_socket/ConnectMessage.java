package nl.andrewl.railsignalapi.live.tcp_socket;

/**
 * A message that's sent in response to a client's attempt to connect.
 * @param valid Whether the connection is valid.
 * @param message A message.
 */
public record ConnectMessage(
		boolean valid,
		String message
) {}
