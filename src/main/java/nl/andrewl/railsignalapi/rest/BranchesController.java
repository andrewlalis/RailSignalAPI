package nl.andrewl.railsignalapi.rest;

import lombok.RequiredArgsConstructor;
import nl.andrewl.railsignalapi.rest.dto.BranchResponse;
import nl.andrewl.railsignalapi.rest.dto.SignalResponse;
import nl.andrewl.railsignalapi.service.BranchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/railSystems/{rsId}/branches")
@RequiredArgsConstructor
public class BranchesController {
	private final BranchService branchService;

	@GetMapping
	public List<BranchResponse> getAllBranches(@PathVariable long rsId) {
		return branchService.getAllBranches(rsId);
	}

	@GetMapping(path = "/{branchId}")
	public BranchResponse getBranch(@PathVariable long rsId, @PathVariable long branchId) {
		return branchService.getBranch(rsId, branchId);
	}

	@GetMapping(path = "/{branchId}/signals")
	public List<SignalResponse> getBranchSignals(@PathVariable long rsId, @PathVariable long branchId) {
		return branchService.getConnectedSignals(rsId, branchId);
	}

	@DeleteMapping(path = "/{branchId}")
	public ResponseEntity<?> deleteBranch(@PathVariable long rsId, @PathVariable long branchId) {
		branchService.deleteBranch(rsId, branchId);
		return ResponseEntity.noContent().build();
	}
}
