package nl.andrewl.railsignalapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.dao.BranchRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.dao.SignalBranchConnectionRepository;
import nl.andrewl.railsignalapi.dao.SignalRepository;
import nl.andrewl.railsignalapi.model.*;
import nl.andrewl.railsignalapi.model.component.SignalBranchConnection;
import nl.andrewl.railsignalapi.rest.dto.SignalConnectionsUpdatePayload;
import nl.andrewl.railsignalapi.rest.dto.SignalCreationPayload;
import nl.andrewl.railsignalapi.rest.dto.SignalResponse;
import nl.andrewl.railsignalapi.websocket.BranchUpdateMessage;
import nl.andrewl.railsignalapi.websocket.SignalUpdateMessage;
import nl.andrewl.railsignalapi.websocket.SignalUpdateType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignalService {
	private final RailSystemRepository railSystemRepository;
	private final SignalRepository signalRepository;
	private final BranchRepository branchRepository;
	private final SignalBranchConnectionRepository signalBranchConnectionRepository;

	private final ObjectMapper mapper = new ObjectMapper();
	private final Map<WebSocketSession, Set<Long>> signalWebSocketSessions = new ConcurrentHashMap<>();

	@Transactional
	public SignalResponse createSignal(long rsId, SignalCreationPayload payload) {
		var rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rail system not found."));
		if (signalRepository.existsByNameAndRailSystem(payload.name(), rs)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Signal " + payload.name() + " already exists.");
		}
		if (payload.branchConnections().size() != 2) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exactly two branch connections must be provided.");
		}
		// Ensure that the directions of the connections are opposite each other.
		Direction dir1 = Direction.parse(payload.branchConnections().get(0).direction());
		Direction dir2 = Direction.parse(payload.branchConnections().get(1).direction());
		if (!dir1.isOpposite(dir2)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Branch connections must be opposite each other.");

		Set<SignalBranchConnection> branchConnections = new HashSet<>();
		Signal signal = new Signal(rs, payload.name(), payload.position(), branchConnections);
		for (var branchData : payload.branchConnections()) {
			Branch branch;
			if (branchData.id() != null) {
				branch = this.branchRepository.findByIdAndRailSystem(branchData.id(), rs)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Branch id " + branchData.id() + " is invalid."));
			} else {
				branch = this.branchRepository.findByNameAndRailSystem(branchData.name(), rs)
						.orElse(new Branch(rs, branchData.name(), BranchStatus.FREE));
			}
			Direction dir = Direction.parse(branchData.direction());
			branchConnections.add(new SignalBranchConnection(signal, branch, dir));
		}
		signal = signalRepository.save(signal);
		return new SignalResponse(signal);
	}

	@Transactional
	public SignalResponse updateSignalBranchConnections(long rsId, long sigId, SignalConnectionsUpdatePayload payload) {
		var signal = signalRepository.findByIdAndRailSystemId(sigId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		for (var c : payload.connections()) {
			var fromConnection = signalBranchConnectionRepository.findById(c.from())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find signal branch connection: " + c.from()));
			if (!fromConnection.getSignal().getId().equals(signal.getId())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only update signal branch connections originating from the specified signal.");
			}
			var toConnection = signalBranchConnectionRepository.findById(c.to())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find signal branch connection: " + c.to()));
			if (!fromConnection.getBranch().getId().equals(toConnection.getBranch().getId())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Signal branch connections can only path via a mutual branch.");
			}
			fromConnection.getReachableSignalConnections().add(toConnection);
			signalBranchConnectionRepository.save(fromConnection);
		}
		for (var con : signal.getBranchConnections()) {
			Set<SignalBranchConnection> connectionsToRemove = new HashSet<>();
			for (var reachableCon : con.getReachableSignalConnections()) {
				if (!payload.connections().contains(new SignalConnectionsUpdatePayload.ConnectionData(con.getId(), reachableCon.getId()))) {
					connectionsToRemove.add(reachableCon);
				}
			}
			con.getReachableSignalConnections().removeAll(connectionsToRemove);
			signalBranchConnectionRepository.save(con);
		}
		// Reload the signal.
		signal = signalRepository.findById(signal.getId()).orElseThrow();
		return new SignalResponse(signal);
	}

	@Transactional
	public void registerSignalWebSocketSession(Set<Long> signalIds, WebSocketSession session) {
		this.signalWebSocketSessions.put(session, signalIds);
		// Instantly send a data packet so that the signals are up-to-date.
		RailSystem rs = null;
		for (var signalId : signalIds) {
			var signal = signalRepository.findById(signalId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signal id."));
			if (rs == null) {
				rs = signal.getRailSystem();
			} else if (!rs.getId().equals(signal.getRailSystem().getId())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot open signal websocket session for signals from different rail systems.");
			}
			for (var branchConnection : signal.getBranchConnections()) {
				try {
					session.sendMessage(new TextMessage(mapper.writeValueAsString(
							new BranchUpdateMessage(
									branchConnection.getBranch().getId(),
									branchConnection.getBranch().getStatus().name()
							)
					)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			signal.setOnline(true);
			signalRepository.save(signal);
		}
	}

	@Transactional
	public void deregisterSignalWebSocketSession(WebSocketSession session) {
		var ids = this.signalWebSocketSessions.remove(session);
		if (ids != null) {
			for (var signalId : ids) {
				signalRepository.findById(signalId).ifPresent(signal -> {
					signal.setOnline(false);
					signalRepository.save(signal);
				});
			}
		}
	}

	public WebSocketSession getSignalWebSocketSession(long signalId) {
		for (var entry : signalWebSocketSessions.entrySet()) {
			if (entry.getValue().contains(signalId)) return entry.getKey();
		}
		return null;
	}

	@Transactional
	public void handleSignalUpdate(SignalUpdateMessage updateMessage) {
		var signal = signalRepository.findById(updateMessage.signalId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Branch fromBranch = null;
		Branch toBranch = null;
		for (var con : signal.getBranchConnections()) {
			if (con.getBranch().getId() == updateMessage.fromBranchId()) {
				fromBranch = con.getBranch();
			}
			if (con.getBranch().getId() == updateMessage.toBranchId()) {
				toBranch = con.getBranch();
			}
		}
		if (fromBranch == null || toBranch == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid branches.");
		}
		SignalUpdateType updateType = SignalUpdateType.valueOf(updateMessage.type().trim().toUpperCase());
		if (updateType == SignalUpdateType.BEGIN && toBranch.getStatus() != BranchStatus.FREE) {
			log.warn("Warning! Train is entering a non-free branch {}.", toBranch.getName());
		}
		if (toBranch.getStatus() != BranchStatus.OCCUPIED) {
			log.info("Updating branch {} status from {} to {}.", toBranch.getName(), toBranch.getStatus(), BranchStatus.OCCUPIED);
			toBranch.setStatus(BranchStatus.OCCUPIED);
			branchRepository.save(toBranch);
			broadcastToConnectedSignals(toBranch);
		}
		if (updateType == SignalUpdateType.END) {
			if (fromBranch.getStatus() != BranchStatus.FREE) {
				log.info("Updating branch {} status from {} to {}.", fromBranch.getName(), fromBranch.getStatus(), BranchStatus.FREE);
				fromBranch.setStatus(BranchStatus.FREE);
				branchRepository.save(fromBranch);
				broadcastToConnectedSignals(fromBranch);
			}
		} else if (updateType == SignalUpdateType.BEGIN) {
			if (fromBranch.getStatus() != BranchStatus.OCCUPIED) {
				log.info("Updating branch {} status from {} to {}.", fromBranch.getName(), fromBranch.getStatus(), BranchStatus.OCCUPIED);
				fromBranch.setStatus(BranchStatus.OCCUPIED);
				branchRepository.save(fromBranch);
				broadcastToConnectedSignals(fromBranch);
			}
		}
	}

	private void broadcastToConnectedSignals(Branch branch) {
		try {
			WebSocketMessage<String> msg = new TextMessage(mapper.writeValueAsString(
					new BranchUpdateMessage(branch.getId(), branch.getStatus().name())
			));
			signalRepository.findAllConnectedToBranch(branch).stream()
					.map(s -> getSignalWebSocketSession(s.getId()))
					.filter(Objects::nonNull).distinct()
					.forEach(session -> {
						try {
							session.sendMessage(msg);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}

	@Transactional(readOnly = true)
	public SignalResponse getSignal(long rsId, long sigId) {
		var s = signalRepository.findByIdAndRailSystemId(sigId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return new SignalResponse(s);
	}

	@Transactional(readOnly = true)
	public List<SignalResponse> getAllSignals(long rsId) {
		var rs = railSystemRepository.findById(rsId)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rail system not found."));
		return signalRepository.findAllByRailSystemOrderByName(rs).stream()
				.map(SignalResponse::new)
				.toList();
	}

	@Transactional
	public void deleteSignal(long rsId, long sigId) {
		var s = signalRepository.findByIdAndRailSystemId(sigId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		signalRepository.delete(s);
	}
}
