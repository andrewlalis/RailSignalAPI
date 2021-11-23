package nl.andrewl.railsignalapi.rest.dto;

import nl.andrewl.railsignalapi.model.Signal;
import nl.andrewl.railsignalapi.model.SignalBranchConnection;

import java.util.Comparator;
import java.util.List;

public record SignalResponse(
		long id,
		String name,
		List<ConnectionData> branchConnections,
		boolean online
) {
	public SignalResponse(Signal signal) {
		this(
				signal.getId(),
				signal.getName(),
				signal.getBranchConnections().stream()
						.sorted(Comparator.comparing(SignalBranchConnection::getDirection))
						.map(ConnectionData::new)
						.toList(),
				signal.isOnline()
		);
	}

	public static record ConnectionData(
			String direction,
			BranchResponse branch
	) {
		public ConnectionData(SignalBranchConnection c) {
			this(c.getDirection().name(), new BranchResponse(c.getBranch()));
		}
	}
}
