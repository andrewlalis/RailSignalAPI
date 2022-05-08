package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.Label;
import nl.andrewl.railsignalapi.model.RailSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
	List<Label> findAllByRailSystem(RailSystem rs);
}
