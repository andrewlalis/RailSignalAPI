package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.RailSystem;
import nl.andrewl.railsignalapi.model.component.Component;
import nl.andrewl.railsignalapi.model.component.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentRepository<T extends Component> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
	Optional<T> findByIdAndRailSystemId(long id, long rsId);

	boolean existsByNameAndRailSystem(String name, RailSystem rs);
	boolean existsByNameAndRailSystemId(String name, long rsId);

	@Query("SELECT c FROM Component c " +
			"WHERE c.railSystem = :rs AND " +
			"c.position.x >= :#{#lower.x} AND c.position.y >= :#{#lower.y} AND c.position.z >= :#{#lower.z} AND " +
			"c.position.x <= :#{#upper.x} AND c.position.y <= :#{#upper.y} AND c.position.z <= :#{#upper.z}")
	List<T> findAllInBounds(RailSystem rs, Position lower, Position upper);

	List<T> findAllByRailSystem(RailSystem rs);

	void deleteAllByRailSystem(RailSystem rs);
}
