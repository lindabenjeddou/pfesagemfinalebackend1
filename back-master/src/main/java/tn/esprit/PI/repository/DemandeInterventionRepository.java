package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.PI.entity.DemandeIntervention;
import tn.esprit.PI.entity.StatutDemande;

import java.util.List;

@Repository
public interface DemandeInterventionRepository extends JpaRepository<DemandeIntervention, Long> {
    
    @Query("SELECT COUNT(d) FROM DemandeIntervention d WHERE d.demandeur.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT d FROM DemandeIntervention d WHERE d.demandeur.id = :userId")
    List<DemandeIntervention> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(d) FROM DemandeIntervention d WHERE d.demandeur.id = :userId AND d.statut = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") StatutDemande status);
    
    @Query("SELECT d FROM DemandeIntervention d WHERE d.demandeur.id = :userId AND d.statut = :status")
    List<DemandeIntervention> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") StatutDemande status);
    
    @Query("SELECT COUNT(d) FROM DemandeIntervention d WHERE d.statut = :status")
    Long countByStatus(@Param("status") StatutDemande status);
    
    @Query("SELECT d FROM DemandeIntervention d ORDER BY d.dateDemande DESC")
    List<DemandeIntervention> findAllOrderByDateCreationDesc();
}
