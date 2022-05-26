package nl.andrewl.railsignalapi.live.dto;

import lombok.Getter;

/**
 * A message that's sent to signal components regarding a change in a segment's
 * status.
 */
@Getter
public class SegmentStatusMessage extends ComponentMessage {
	/**
	 * The id of the segment that updated.
	 */
	private final long sId;

	/**
	 * Whether the segment is occupied.
	 */
	private final boolean occupied;

	public SegmentStatusMessage(long cId, long sId, boolean occupied) {
		super(cId, "SEGMENT_STATUS");
		this.sId = sId;
		this.occupied = occupied;
	}
}
