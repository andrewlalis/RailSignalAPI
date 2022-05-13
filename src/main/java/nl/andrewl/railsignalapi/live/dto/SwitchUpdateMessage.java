package nl.andrewl.railsignalapi.live.dto;

import java.util.Set;

/**
 * Message that's sent by a switch when its active configuration is updated.
 */
public class SwitchUpdateMessage extends ComponentUplinkMessage {
	/**
	 * A set of path node ids that represents the active configuration.
	 */
	public Set<Long> configuration;
}
