package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.component.PathNode;
import nl.andrewl.railsignalapi.model.component.SwitchConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwitchConfigurationRepository extends JpaRepository<SwitchConfiguration, Long> {
	void deleteAllByNodesContaining(PathNode p);
}
