package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.component.Switch;
import org.springframework.stereotype.Repository;

@Repository
public interface SwitchRepository extends ComponentRepository<Switch> {
}
