package nl.andrewl.railsignalapi.live.dto;

public record SegmentStatusMessage (
		long cId,
		boolean occupied
) {}
