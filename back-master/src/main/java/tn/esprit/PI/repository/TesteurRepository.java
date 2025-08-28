package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.PI.entity.Testeur;

import java.util.List;

public interface TesteurRepository extends JpaRepository<Testeur, String> {
    List<Testeur> findByAtelierAndLigne(String atelier, String ligne);
}
