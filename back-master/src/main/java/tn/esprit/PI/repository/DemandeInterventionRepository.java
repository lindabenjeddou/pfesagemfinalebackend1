package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.PI.entity.DemandeIntervention;

import java.util.List;
import java.util.Map;

@Repository
public interface DemandeInterventionRepository extends JpaRepository<DemandeIntervention, Long> {

    /** Toutes les demandes (dates null-safe) */
    @Query(value =
            "SELECT id, description, date_demande, statut, priorite, demandeur, type_demande, " +
                    "CASE WHEN date_creation = '0000-00-00 00:00:00' THEN NULL ELSE date_creation END AS date_creation, " +
                    "CASE WHEN date_validation = '0000-00-00 00:00:00' THEN NULL ELSE date_validation END AS date_validation, " +
                    "COALESCE(confirmation, 0) AS confirmation, " +
                    "testeur_code_gmao, technicien_id, " +
                    "panne, urgence, frequence, prochainrdv " +
                    "FROM demande_intervention",
            nativeQuery = true)
    List<Map<String, Object>> findAllWithNullSafeDates();

    /** Demandes assignées à un technicien (dates null-safe) */
    @Query(value =
            "SELECT id, description, date_demande, statut, priorite, demandeur, type_demande, " +
                    "CASE WHEN date_creation = '0000-00-00 00:00:00' THEN NULL ELSE date_creation END AS date_creation, " +
                    "CASE WHEN date_validation = '0000-00-00 00:00:00' THEN NULL ELSE date_validation END AS date_validation, " +
                    "COALESCE(confirmation, 0) AS confirmation, " +
                    "testeur_code_gmao, technicien_id, " +
                    "panne, urgence, frequence, prochainrdv " +
                    "FROM demande_intervention " +
                    "WHERE technicien_id = ?1",
            nativeQuery = true)
    List<Map<String, Object>> findAllByTechnicienIdWithNullSafeDates(Long technicienId);

    /** Mise à jour basique de champs principaux */
    @Modifying
    @Transactional
    @Query(value = "UPDATE demande_intervention SET " +
            "description = COALESCE(?2, description), " +
            "statut = COALESCE(?3, statut), " +
            "priorite = COALESCE(?4, priorite), " +
            "technicien_id = COALESCE(?5, technicien_id) " +
            "WHERE id = ?1",
            nativeQuery = true)
    int updateDemandeBasicFields(Long id, String description, String statut, String priorite, Long technicienId);

    /** Confirmer l’intervention */
    @Modifying
    @Transactional
    @Query(value = "UPDATE demande_intervention SET " +
            "confirmation = 1, " +
            "date_validation = NOW(), " +
            "statut = 'TERMINEE' " +
            "WHERE id = ?1",
            nativeQuery = true)
    int confirmerInterventionNative(Long id);

    /** Assigner un technicien */
    @Modifying
    @Transactional
    @Query(value = "UPDATE demande_intervention SET " +
            "technicien_id = ?2, " +
            "statut = CASE WHEN statut = 'EN_ATTENTE' THEN 'EN_COURS' ELSE statut END " +
            "WHERE id = ?1",
            nativeQuery = true)
    int assignTechnicianNative(Long interventionId, Long technicienId);

    /** Assigner un testeur/équipement */
    @Modifying
    @Transactional
    @Query(value = "UPDATE demande_intervention SET " +
            "testeur_code_gmao = ?2 " +
            "WHERE id = ?1",
            nativeQuery = true)
    int assignTesteurNative(Long interventionId, String testeurCodeGMAO);
}
