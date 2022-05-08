package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.component.Signal;
import org.springframework.stereotype.Repository;

@Repository
public interface SignalRepository extends ComponentRepository<Signal> {
}
