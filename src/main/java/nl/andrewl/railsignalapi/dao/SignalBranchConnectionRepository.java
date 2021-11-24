package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.SignalBranchConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignalBranchConnectionRepository extends JpaRepository<SignalBranchConnection, Long> {
}
