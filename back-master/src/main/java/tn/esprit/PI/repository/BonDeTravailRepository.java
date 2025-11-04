package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.PI.entity.BonDeTravail;
import tn.esprit.PI.entity.StatutBonTravail;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BonDeTravailRepository extends JpaRepository<BonDeTravail, Long> {

    @Modifying
    @Query(value = "DELETE FROM bon_travail_composants WHERE bon_id = :bonId", nativeQuery = true)
    void deleteComponentsByBonId(@Param("bonId") Long bonId);

    List<BonDeTravail> findByDateCreationBetween(LocalDate dateDebut, LocalDate dateFin);

    List<BonDeTravail> findByStatutAndDateCreationBetween(StatutBonTravail statut, LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT COUNT(b) FROM BonDeTravail b WHERE b.statut = :statut AND b.dateCreation BETWEEN :dateDebut AND :dateFin")
    Long countByStatutAndDateCreationBetween(@Param("statut") StatutBonTravail statut, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    // Méthodes pour les nouvelles associations
    List<BonDeTravail> findByInterventionId(Long interventionId);

    List<BonDeTravail> findByTesteurCodeGMAO(String testeurCodeGMAO);
}
