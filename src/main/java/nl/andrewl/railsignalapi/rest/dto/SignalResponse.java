package nl.andrewl.railsignalapi.rest.dto;

import nl.andrewl.railsignalapi.model.Position;
import nl.andrewl.railsignalapi.model.Signal;
import nl.andrewl.railsignalapi.model.SignalBranchConnection;

import java.util.Comparator;
import java.util.List;

public record SignalResponse(
		long id,
		String name,
		Position position,
		List<ConnectionData> branchConnections,
		boolean online
) {
	public SignalResponse(Signal signal) {
		this(
				signal.getId(),
				signal.getName(),
				signal.getPosition(),
				signal.getBranchConnections().stream()
						.sorted(Comparator.comparing(SignalBranchConnection::getDirection))
						.map(ConnectionData::new)
						.toList(),
				signal.isOnline()
		);
	}

	public static record ConnectionData(
			long id,
			String direction,
			BranchResponse branch,
			List<ReachableConnectionData> reachableSignalConnections
	) {
		public ConnectionData(SignalBranchConnection c) {
			this(
					c.getId(),
					c.getDirection().name(),
					new BranchResponse(c.getBranch()),
					c.getReachableSignalConnections().stream()
							.map(cc -> new ReachableConnectionData(
									cc.getId(),
									cc.getSignal().getId(),
									cc.getSignal().getName(),
									cc.getSignal().getPosition()
							))
							.toList()
			);
		}

		public static record ReachableConnectionData(
				long connectionId,
				long signalId,
				String signalName,
				Position signalPosition
		) {}
	}
}
