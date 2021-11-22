package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.Branch;
import nl.andrewl.railsignalapi.model.RailSystem;
import nl.andrewl.railsignalapi.model.Signal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignalRepository extends JpaRepository<Signal, Long> {
	Optional<Signal> findByIdAndRailSystem(long id, RailSystem railSystem);
	Optional<Signal> findByIdAndRailSystemId(long id, long railSystemId);
	boolean existsByNameAndRailSystem(String name, RailSystem railSystem);

	@Query("SELECT DISTINCT s FROM Signal s " +
			"LEFT JOIN s.branchConnections bc " +
			"WHERE bc.branch = :branch")
	List<Signal> findAllConnectedToBranch(Branch branch);

	List<Signal> findAllByRailSystemOrderByName(RailSystem railSystem);
}
