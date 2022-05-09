package nl.andrewl.railsignalapi.dao;

import nl.andrewl.railsignalapi.model.ComponentAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentAccessTokenRepository extends JpaRepository<ComponentAccessToken, Long> {
	Iterable<ComponentAccessToken> findAllByTokenPrefix(String prefix);
	boolean existsByLabel(String label);

}
