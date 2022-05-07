package nl.andrewl.railsignalapi.rest.dto;

import java.util.List;

public class PathNodeUpdatePayload {
	public List<NodeIdObj> connectedNodes;
	public static class NodeIdObj {
		public long id;
	}
}
