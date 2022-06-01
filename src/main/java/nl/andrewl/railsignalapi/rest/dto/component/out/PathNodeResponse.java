package nl.andrewl.railsignalapi.rest.dto.component.out;

import nl.andrewl.railsignalapi.model.component.PathNode;

import java.util.Comparator;
import java.util.List;

public abstract class PathNodeResponse extends ComponentResponse {
	public List<SimpleComponentResponse> connectedNodes;

	public PathNodeResponse(PathNode p) {
		super(p);
		this.connectedNodes = p.getConnectedNodes().stream()
				.sorted(Comparator.comparing(PathNode::getName))
				.map(SimpleComponentResponse::new)
				.toList();
	}
}
