package nl.andrewl.railsignalapi.websocket;

public enum SignalUpdateType {
	/**
	 * Indicates the beginning of a train's transition between two signalled
	 * sections of rail.
	 */
	BEGIN,

	/**
	 * Indicates the end of a train's transition between two signalled sections
	 * of rail.
	 */
	END
}
