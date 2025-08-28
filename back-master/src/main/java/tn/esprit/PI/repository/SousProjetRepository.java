package tn.esprit.PI.repository;

import tn.esprit.PI.entity.SousProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SousProjetRepository extends JpaRepository<SousProjet, Long> {
    List<SousProjet> findByProjectId(Long projectId);
}

