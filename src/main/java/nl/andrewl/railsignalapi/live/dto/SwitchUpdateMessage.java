package nl.andrewl.railsignalapi.live.dto;

import lombok.NoArgsConstructor;

/**
 * Message that's sent by a switch when its active configuration is updated.
 */
@NoArgsConstructor
public class SwitchUpdateMessage extends ComponentMessage {
	/**
	 * The id of the configuration that's active.
	 */
	public long activeConfigId;

	public SwitchUpdateMessage(long cId, long activeConfigId) {
		super(cId, "SWITCH_UPDATE");
		this.activeConfigId = activeConfigId;
	}
}
