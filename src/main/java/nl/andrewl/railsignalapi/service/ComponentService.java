package nl.andrewl.railsignalapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.dao.SegmentRepository;
import nl.andrewl.railsignalapi.dao.SwitchConfigurationRepository;
import nl.andrewl.railsignalapi.model.RailSystem;
import nl.andrewl.railsignalapi.model.Segment;
import nl.andrewl.railsignalapi.model.component.*;
import nl.andrewl.railsignalapi.rest.dto.PathNodeUpdatePayload;
import nl.andrewl.railsignalapi.rest.dto.component.out.ComponentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ComponentService {
	private final ComponentRepository<Component> componentRepository;
	private final RailSystemRepository railSystemRepository;
	private final SegmentRepository segmentRepository;
	private final SwitchConfigurationRepository switchConfigurationRepository;

	@Transactional(readOnly = true)
	public List<ComponentResponse> getComponents(long rsId) {
		var rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return componentRepository.findAllByRailSystem(rs).stream().map(ComponentResponse::of).toList();
	}

	@Transactional(readOnly = true)
	public ComponentResponse getComponent(long rsId, long componentId) {
		var c = componentRepository.findByIdAndRailSystemId(componentId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return ComponentResponse.of(c);
	}

	@Transactional
	public void removeComponent(long rsId, long componentId) {
		var c = componentRepository.findByIdAndRailSystemId(componentId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		// If this is a path node, check for and remove any switch configurations that use it.
		if (c instanceof PathNode p) {
			switchConfigurationRepository.deleteAllByNodesContaining(p);
		}
		componentRepository.delete(c);
	}

	@Transactional
	public ComponentResponse create(long rsId, ObjectNode data) {
		RailSystem rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		String type = data.get("type").asText();
		Position pos = new Position();
		pos.setX(data.get("position").get("x").asDouble());
		pos.setY(data.get("position").get("y").asDouble());
		pos.setZ(data.get("position").get("z").asDouble());
		String name = data.get("name").asText();

		if (name != null && componentRepository.existsByNameAndRailSystem(name, rs)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component with that name already exists.");
		}

		Component c = switch (type) {
			case "SIGNAL" -> createSignal(rs, pos, name, data);
			case "SWITCH" -> createSwitch(rs, pos, name, data);
			case "SEGMENT_BOUNDARY" -> createSegmentBoundary(rs, pos, name, data);
			default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported component type: " + type);
		};
		c = componentRepository.save(c);
		return ComponentResponse.of(c);
	}

	private Component createSignal(RailSystem rs, Position pos, String name, ObjectNode data) {
		long segmentId = data.get("segment").get("id").asLong();
		Segment segment = segmentRepository.findById(segmentId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return new Signal(rs, pos, name, segment);
	}

	private Component createSwitch(RailSystem rs, Position pos, String name, ObjectNode data) {
		Switch s = new Switch(rs, pos, name, new HashSet<>(), new HashSet<>(), null);
		for (JsonNode configJson : data.withArray("possibleConfigurations")) {
			Set<PathNode> pathNodes = new HashSet<>();
			for (JsonNode pathNodeJson : configJson.withArray("nodes")) {
				long pathNodeId = pathNodeJson.get("id").asLong();
				Component c = componentRepository.findById(pathNodeId)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
				if (c instanceof PathNode pathNode) {
					pathNodes.add(pathNode);
					s.getConnectedNodes().add(pathNode);
				} else {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id " + pathNodeId + " does not refer to a PathNode component.");
				}
			}
			s.getPossibleConfigurations().add(new SwitchConfiguration(s, pathNodes));
		}
		return s;
	}

	private Component createSegmentBoundary(RailSystem rs, Position pos, String name, ObjectNode data) {
		ArrayNode segmentsNode = data.withArray("segments");
		Set<Segment> segments = new HashSet<>();
		for (JsonNode segmentNode : segmentsNode) {
			long segmentId = segmentNode.get("id").asLong();
			Segment segment = segmentRepository.findById(segmentId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			segments.add(segment);
		}
		if (segments.size() < 1 || segments.size() > 2) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid number of segments.");
		}
		return new SegmentBoundaryNode(rs, pos, name, new HashSet<>(), segments);
	}

	@Transactional
	public ComponentResponse updatePath(long rsId, long cId, PathNodeUpdatePayload payload) {
		var c = componentRepository.findByIdAndRailSystemId(cId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!(c instanceof PathNode p)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component is not a PathNode.");
		Set<PathNode> newNodes = new HashSet<>();
		for (var id : payload.connectedNodeIds()) {
			var c1 = componentRepository.findByIdAndRailSystemId(id, rsId);
			if (c1.isPresent() && c1.get() instanceof PathNode pn) {
				newNodes.add(pn);
			} else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component with id " + id + " is not a PathNode in the same rail system.");
			}
		}
		p.getConnectedNodes().retainAll(newNodes);
		p.getConnectedNodes().addAll(newNodes);
		p = componentRepository.save(p);
		return ComponentResponse.of(p);
	}
}
