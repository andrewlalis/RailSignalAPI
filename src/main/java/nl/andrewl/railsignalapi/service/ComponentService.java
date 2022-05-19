package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.ComponentRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.dao.SwitchConfigurationRepository;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.model.component.PathNode;
import nl.andrewl.railsignalapi.rest.dto.PathNodeUpdatePayload;
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
	public ComponentResponse updatePath(long rsId, long cId, PathNodeUpdatePayload payload) {
		var c = componentRepository.findByIdAndRailSystemId(cId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!(c instanceof PathNode p)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component is not a PathNode.");
		Set<PathNode> newNodes = new HashSet<>();
		for (var nodeObj : payload.connectedNodes) {
			long id = nodeObj.id;
			var c1 = componentRepository.findByIdAndRailSystemId(id, rsId);
			if (c1.isPresent() && c1.get() instanceof PathNode pn) {
				newNodes.add(pn);
			} else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Component with id " + id + " is not a PathNode in the same rail system.");
			}
		}

		Set<PathNode> nodesToRemove = new HashSet<>(p.getConnectedNodes());
		nodesToRemove.removeAll(newNodes);

		Set<PathNode> nodesToAdd = new HashSet<>(newNodes);
		nodesToAdd.removeAll(p.getConnectedNodes());

		p.getConnectedNodes().removeAll(nodesToRemove);
		p.getConnectedNodes().addAll(nodesToAdd);
		for (var node : nodesToRemove) {
			node.getConnectedNodes().remove(p);
		}
		for (var node : nodesToAdd) {
			node.getConnectedNodes().add(p);
		}
		componentRepository.saveAll(nodesToRemove);
		componentRepository.saveAll(nodesToAdd);
		p = componentRepository.save(p);
		return ComponentResponse.of(p);
	}
}
