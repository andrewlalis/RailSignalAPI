package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
}
