package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.RailSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RailSystemRepository extends JpaRepository<RailSystem, Long> {
	boolean existsByName(String name);
}
