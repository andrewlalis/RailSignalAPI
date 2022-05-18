package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.LinkToken;
import nl.andrewl.railsignalapi.model.RailSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkTokenRepository extends JpaRepository<LinkToken, Long> {
	Iterable<LinkToken> findAllByTokenPrefix(String prefix);
	boolean existsByLabel(String label);
	List<LinkToken> findAllByRailSystem(RailSystem rs);
	Optional<LinkToken> findByIdAndRailSystemId(long ltId, long rsId);
}
