package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.LinkToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkTokenRepository extends JpaRepository<LinkToken, Long> {
	Iterable<LinkToken> findAllByTokenPrefix(String prefix);
	boolean existsByLabel(String label);

}
