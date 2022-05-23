package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.dao.SegmentRepository;
import nl.andrewl.railsignalapi.dao.SwitchConfigurationRepository;
import nl.andrewl.railsignalapi.model.Segment;
import nl.andrewl.railsignalapi.model.component.*;
import nl.andrewl.railsignalapi.rest.dto.component.in.*;
import nl.andrewl.railsignalapi.rest.dto.component.out.ComponentResponse;
import nl.andrewl.railsignalapi.rest.dto.component.out.SimpleComponentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ComponentService {
	private final ComponentRepository<Component> componentRepository;
	private final RailSystemRepository railSystemRepository;
	private final SwitchConfigurationRepository switchConfigurationRepository;
	private final SegmentRepository segmentRepository;

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

	@Transactional(readOnly = true)
	public Page<SimpleComponentResponse> search(long rsId, String searchQuery, Pageable pageable) {
		return componentRepository.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>(2);
			predicates.add(cb.equal(root.get("railSystem").get("id"), rsId));
			if (searchQuery != null && !searchQuery.isBlank()) {
				predicates.add(cb.like(cb.lower(root.get("name")), '%' + searchQuery.toLowerCase() + '%'));
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		}, pageable).map(SimpleComponentResponse::new);
	}

	@Transactional
	public void removeComponent(long rsId, long componentId) {
		var c = componentRepository.findByIdAndRailSystemId(componentId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (c instanceof PathNode p) {
			// Remove all connections to other path nodes.
			for (var connectedNode : p.getConnectedNodes()) {
				connectedNode.getConnectedNodes().remove(p);
				componentRepository.save(connectedNode);
			}
			// Remove any switch configurations using this node.
			switchConfigurationRepository.deleteAllByNodesContaining(p);
		}
		componentRepository.delete(c);
	}

	@Transactional
	public ComponentResponse updateComponent(long rsId, long componentId, ComponentPayload payload) {
		var c = componentRepository.findByIdAndRailSystemId(componentId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!c.getType().name().equalsIgnoreCase(payload.type)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update a component's type. Remove and create a new component instead.");
		}
		if (!c.getName().equals(payload.name) && componentRepository.existsByNameAndRailSystemId(payload.name, rsId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot rename component because another component has that name.");
		}
		c.setName(payload.name);
		c.getPosition().setX(payload.position.getX());
		c.getPosition().setY(payload.position.getY());
		c.getPosition().setZ(payload.position.getZ());
		if (c instanceof Signal s && payload instanceof SignalPayload sp) {
			updateSignal(s, sp, rsId);
		}
		if (c instanceof SegmentBoundaryNode sb && payload instanceof SegmentBoundaryPayload sbp) {
			updateSegmentBoundary(sb, sbp, rsId);
		}
		if (c instanceof Label lbl && payload instanceof LabelPayload lp) {
			lbl.setText(lp.text);
		}
		if (c instanceof Switch sw && payload instanceof SwitchPayload sp) {
			updateSwitch(sw, sp, rsId);
		}
		return ComponentResponse.of(componentRepository.save(c));
	}

	private void updateSignal(Signal s, SignalPayload sp, long rsId) {
		Segment segment = segmentRepository.findByIdAndRailSystemId(sp.segment.id, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Signal's attached segment with id " + sp.segment.id + " is invalid."));
		s.setSegment(segment);
	}

	private void updateSegmentBoundary(SegmentBoundaryNode sb, SegmentBoundaryPayload sbp, long rsId) {
		Set<Segment> newSegments = new HashSet<>();
		for (var segData : sbp.segments) {
			newSegments.add(segmentRepository.findByIdAndRailSystemId(segData.id, rsId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Segment boundary's attached segment with id " + segData.id + " is invalid.")));
		}
		sb.getSegments().retainAll(newSegments);
		sb.getSegments().addAll(newSegments);

		if (sbp.connectedNodes != null) {
			Set<PathNode> connectedNodes = new HashSet<>();
			for (var nodeData : sbp.connectedNodes) {
				var component = componentRepository.findByIdAndRailSystemId(nodeData.id, rsId)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path node: " + nodeData.id));
				if (component instanceof PathNode pathNode && !component.equals(sb)) {
					connectedNodes.add(pathNode);
				} else {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path node: " + nodeData.id);
				}
			}
			updateConnectedNodes(sb, connectedNodes);
		}
	}

	private void updateSwitch(Switch sw, SwitchPayload sp, long rsId) {
		Set<SwitchConfiguration> newConfigs = new HashSet<>();
		Set<PathNode> connectedNodes = new HashSet<>();
		for (var configData : sp.possibleConfigurations) {
			Set<PathNode> nodes = new HashSet<>();
			for (var nodeData : configData.nodes) {
				var component = componentRepository.findByIdAndRailSystemId(nodeData.id, rsId)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path node id: " + nodeData.id));
				if (component instanceof PathNode pathNode && !pathNode.getId().equals(sw.getId())) {
					nodes.add(pathNode);
					connectedNodes.add(pathNode);
				} else {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path node: " + nodeData.id);
				}
			}
			if (nodes.size() != 2) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Switch configuration doesn't have required 2 path nodes.");
			}
			newConfigs.add(new SwitchConfiguration(sw, nodes));
		}
		if (newConfigs.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There must be at least one switch configuration.");
		}

		sw.getPossibleConfigurations().retainAll(newConfigs);
		sw.getPossibleConfigurations().addAll(newConfigs);

		// A switch's connectedNodes are derived from its set of possible configurations.
		// So now we need to update all connected nodes to match the set of configurations.
		updateConnectedNodes(sw, connectedNodes);
	}

	private void updateConnectedNodes(PathNode owner, Set<PathNode> newNodes) {
		Set<PathNode> disconnected = new HashSet<>(owner.getConnectedNodes());
		disconnected.removeAll(newNodes);

		Set<PathNode> connected = new HashSet<>(newNodes);
		connected.removeAll(owner.getConnectedNodes());

		owner.getConnectedNodes().retainAll(newNodes);
		owner.getConnectedNodes().addAll(newNodes);

		for (var node : disconnected) {
			node.getConnectedNodes().remove(owner);
			componentRepository.save(node);
		}
		for (var node : connected) {
			node.getConnectedNodes().add(owner);
			componentRepository.save(node);
		}
	}
}
