package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.PI.entity.BonDeTravail;

@Repository
public interface BonDeTravailRepository extends JpaRepository<BonDeTravail, Long> {
}
