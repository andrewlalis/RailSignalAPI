package nl.andrewl.railsignalapi.rest.dto;

import nl.andrewl.railsignalapi.model.Branch;

public record BranchResponse(
		long id,
		String name,
		String status
) {
	public BranchResponse(Branch branch) {
		this(branch.getId(), branch.getName(), branch.getStatus().name());
	}
}
