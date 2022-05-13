package nl.andrewl.railsignalapi.live.dto;

/**
 * Message that's sent by segment boundaries when a train crosses it.
 */
public class SegmentBoundaryUpdateMessage extends ComponentUplinkMessage {
	/**
	 * The id of the segment that a train detected by the segment boundary is
	 * moving towards.
	 */
	public long toSegmentId;

	/**
	 * The type of boundary crossing event.
	 */
	public Type eventType;

	public enum Type {
		/**
		 * Used when a train first begins to enter a segment, which means the
		 * train is now transitioning from its previous to next segment.
		 */
		ENTERING,
		/**
		 * Used when a train has completely entered a segment, which means it
		 * is completely out of its previous segment.
		 */
		ENTERED
	}
}
