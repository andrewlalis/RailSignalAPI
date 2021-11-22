package nl.andrewl.railsignalapi.service;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.dao.BranchRepository;
import nl.andrewl.railsignalapi.dao.RailSystemRepository;
import nl.andrewl.railsignalapi.rest.dto.BranchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {
	private final BranchRepository branchRepository;
	private final RailSystemRepository railSystemRepository;

	@Transactional
	public void deleteBranch(long rsId, long branchId) {
		var branch = branchRepository.findByIdAndRailSystemId(branchId, rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!branch.getSignalConnections().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Branch should not be connected to any signals.");
		}
		branchRepository.delete(branch);
	}

	@Transactional(readOnly = true)
	public List<BranchResponse> getAllBranches(long rsId) {
		var rs = railSystemRepository.findById(rsId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return branchRepository.findAllByRailSystemOrderByName(rs).stream()
				.map(BranchResponse::new)
				.toList();
	}
}
