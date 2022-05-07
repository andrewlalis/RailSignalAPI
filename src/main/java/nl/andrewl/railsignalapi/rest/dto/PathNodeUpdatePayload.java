package nl.andrewl.railsignalapi.rest.dto;

public record PathNodeUpdatePayload (
		long[] connectedNodeIds
) {}
