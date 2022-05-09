package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.dao.SegmentRepository;
import nl.andrewl.railsignalapi.model.Label;
import nl.andrewl.railsignalapi.model.RailSystem;
import nl.andrewl.railsignalapi.model.Segment;
import nl.andrewl.railsignalapi.model.component.*;
import nl.andrewl.railsignalapi.rest.dto.component.in.*;
import nl.andrewl.railsignalapi.rest.dto.component.out.ComponentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ComponentCreationService {
	private final RailSystemRepository railSystemRepository;
	private final ComponentRepository<Component> componentRepository;
	private final SegmentRepository segmentRepository;

	@Transactional
	public ComponentResponse create(long rsId, ComponentPayload payload) {
		RailSystem rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		String name = payload.name;
		if (name == null || name.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required name.");
		}
		if (componentRepository.existsByNameAndRailSystem(name, rs)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component with that name already exists.");
		}
		ComponentType type;
		try {
			type = ComponentType.valueOf(payload.type);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid component type.", e);
		}

		Component c = switch (type) {
			case SIGNAL -> createSignal(rs, (SignalPayload) payload);
			case SWITCH -> createSwitch(rs, (SwitchPayload) payload);
			case SEGMENT_BOUNDARY -> createSegmentBoundary(rs, (SegmentBoundaryPayload) payload);
			case LABEL -> createLabel(rs, (LabelPayload) payload);
		};
		c = componentRepository.save(c);
		return ComponentResponse.of(c);
	}

	private Component createLabel(RailSystem rs, LabelPayload payload) {
		return new Label(rs, payload.position, payload.name, payload.text);
	}

	private Component createSignal(RailSystem rs, SignalPayload payload) {
		long segmentId = payload.segment.id;
		Segment segment = segmentRepository.findById(segmentId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return new Signal(rs, payload.position, payload.name, segment);
	}

	private Component createSwitch(RailSystem rs, SwitchPayload payload) {
		Switch s = new Switch(rs, payload.position, payload.name, new HashSet<>(), new HashSet<>(), null);
		for (var config : payload.possibleConfigurations) {
			Set<PathNode> pathNodes = new HashSet<>();
			for (var node : config.nodes) {
				Component c = componentRepository.findById(node.id)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
				if (c instanceof PathNode pathNode) {
					pathNodes.add(pathNode);
					s.getConnectedNodes().add(pathNode);
				} else {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id " + node.id + " does not refer to a PathNode component.");
				}
			}
			s.getPossibleConfigurations().add(new SwitchConfiguration(s, pathNodes));
		}
		if (s.getPossibleConfigurations().size() < 2) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least two switch configurations are needed.");
		}
		return s;
	}

	private Component createSegmentBoundary(RailSystem rs, SegmentBoundaryPayload payload) {
		Set<Segment> segments = new HashSet<>();
		for (var segmentP : payload.segments) {
			Segment segment = segmentRepository.findById(segmentP.id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			segments.add(segment);
		}
		if (segments.size() < 1 || segments.size() > 2) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid number of segments.");
		}
		return new SegmentBoundaryNode(rs, payload.position, payload.name, new HashSet<>(), segments);
	}
}
