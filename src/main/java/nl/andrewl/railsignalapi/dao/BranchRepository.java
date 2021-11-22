package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.Branch;
import nl.andrewl.railsignalapi.model.RailSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
	Optional<Branch> findByIdAndRailSystem(long id, RailSystem railSystem);
	Optional<Branch> findByIdAndRailSystemId(long id, long railSystemId);
	Optional<Branch> findByNameAndRailSystem(String name, RailSystem railSystem);
	List<Branch> findAllByRailSystemOrderByName(RailSystem railSystem);
	List<Branch> findAllByNameAndRailSystem(String name, RailSystem railSystem);
}
