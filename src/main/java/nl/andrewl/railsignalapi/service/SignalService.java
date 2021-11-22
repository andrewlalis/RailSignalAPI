package nl.andrewl.railsignalapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.dao.BranchRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.dao.SignalRepository;
import nl.andrewl.railsignalapi.model.*;
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

	private final Map<Long, WebSocketSession> signalWebSocketSessions = new ConcurrentHashMap<>();

	@Transactional
	public SignalResponse createSignal(long rsId, SignalCreationPayload payload) {
		var rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rail system not found."));
		if (signalRepository.existsByNameAndRailSystem(payload.name(), rs)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Signal " + payload.name() + " already exists.");
		}
		Set<SignalBranchConnection> branchConnections = new HashSet<>();
		Signal signal = new Signal(rs, payload.name(), branchConnections);
		for (var branchData : payload.branchConnections()) {
			Branch branch;
			if (branchData.id() != null) {
				branch = this.branchRepository.findByIdAndRailSystem(branchData.id(), rs)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Branch id " + branchData.id() + " is invalid."));
			} else {
				branch = this.branchRepository.findByNameAndRailSystem(branchData.name(), rs)
						.orElse(new Branch(rs, branchData.name(), BranchStatus.FREE));
			}
			Direction dir = Direction.valueOf(branchData.direction().trim().toUpperCase());
			branchConnections.add(new SignalBranchConnection(signal, branch, dir));
		}
		signal = signalRepository.save(signal);
		return new SignalResponse(signal);
	}

	public void registerSignalWebSocketSession(long signalId, WebSocketSession session) {
		this.signalWebSocketSessions.put(signalId, session);
	}

	public void deregisterSignalWebSocketSession(long signalId) {
		this.signalWebSocketSessions.remove(signalId);
	}

	@Transactional
	public void handleSignalUpdate(long signalId, SignalUpdateMessage updateMessage) {
		var signal = signalRepository.findById(signalId)
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
		}
	}

	private void broadcastToConnectedSignals(Branch branch) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebSocketMessage<String> msg = new TextMessage(mapper.writeValueAsString(new BranchUpdateMessage(branch.getId(), branch.getStatus().name())));
			signalRepository.findAllConnectedToBranch(branch).stream()
					.map(s -> signalWebSocketSessions.get(s.getId()))
					.filter(Objects::nonNull)
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
}
