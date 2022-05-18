package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.RailSystem;
import nl.andrewl.railsignalapi.model.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SegmentRepository extends JpaRepository<Segment, Long> {
	boolean existsByNameAndRailSystem(String name, RailSystem rs);

	List<Segment> findAllByRailSystemIdOrderByName(long rsId);

	Optional<Segment> findByIdAndRailSystemId(long id, long rsId);

	void deleteAllByRailSystem(RailSystem rs);
}
