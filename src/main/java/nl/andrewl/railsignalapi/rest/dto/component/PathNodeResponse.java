package nl.andrewl.railsignalapi.rest.dto.component;

import nl.andrewl.railsignalapi.model.component.PathNode;

import java.util.List;

public abstract class PathNodeResponse extends ComponentResponse {
	public List<SimpleComponentResponse> connectedNodes;

	public PathNodeResponse(PathNode p) {
		super(p);
		this.connectedNodes = p.getConnectedNodes().stream().map(SimpleComponentResponse::new).toList();
	}
}
