package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.PlanningHoraire;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.entity.UserRole;
import tn.esprit.PI.repository.PlanningHoraireRepository;
import tn.esprit.PI.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanningHoraireService {

    @Autowired
    private PlanningHoraireRepository planningHoraireRepository;

    @Autowired
    private UserRepository userRepository;

    // Ajouter ou mettre à jour un planning horaire
    public PlanningHoraire savePlanningHoraire(PlanningHoraire planningHoraire) {
        if (planningHoraire.getUser() == null || planningHoraire.getUser().getId() == null) {
            throw new IllegalArgumentException("L'utilisateur est requis.");
        }

        // Charger l'utilisateur depuis la base
        User user = userRepository.findById(planningHoraire.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec ID : " + planningHoraire.getUser().getId()));

        planningHoraire.setUser(user);
        planningHoraire.setValid(planningHoraire.isValid()); // tu peux forcer à true ici si besoin
        return planningHoraireRepository.save(planningHoraire);
    }

    // Trouver un planning horaire par ID
    public Optional<PlanningHoraire> findById(Long id) {
        return planningHoraireRepository.findById(id);
    }

    // Mettre à jour un planning horaire (utilisé dans /update/{id})
    public PlanningHoraire updatePlanningHoraire(Long id, PlanningHoraire newPlanning) {
        return planningHoraireRepository.findById(id).map(planning -> {
            planning.setStartDate(newPlanning.getStartDate());
            planning.setEndDate(newPlanning.getEndDate());
            planning.setDescription(newPlanning.getDescription());
            planning.setValid(newPlanning.isValid());
            return planningHoraireRepository.save(planning);
        }).orElseThrow(() -> new RuntimeException("Planning non trouvé avec l'ID : " + id));
    }

    // Supprimer un planning horaire
    public void deletePlanningHoraire(Long id) {
        planningHoraireRepository.deleteById(id);
    }

    // Obtenir tous les plannings horaires
    public List<PlanningHoraire> findAllPlanningHoraires() {
        return planningHoraireRepository.findAll();
    }
    
    /**
     * Récupère les techniciens disponibles pour une date donnée
     * Un technicien est disponible si :
     * 1. Il a le rôle TECHNICIEN_CURATIF ou TECHNICIEN_PREVENTIF
     * 2. Il a un planning validé qui couvre la date demandée (il travaille ce jour-là)
     */
    public List<User> getTechniciensDisponibles(LocalDate date) {
        // Début et fin du jour demandé
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        // Récupérer tous les plannings validés qui couvrent cette date
        List<PlanningHoraire> planningsJour = planningHoraireRepository.findAll().stream()
                .filter(p -> p.getValid() != null && p.getValid()) // Seulement les plannings validés
                .filter(p -> p.getUser() != null) // Vérifier que le user existe
                .filter(p -> {
                    // Vérifier si le planning couvre le jour demandé
                    LocalDateTime pStart = p.getStartDate();
                    LocalDateTime pEnd = p.getEndDate();
                    if (pStart == null || pEnd == null) return false;
                    
                    // Le planning couvre le jour si :
                    // - Il commence avant ou pendant le jour ET
                    // - Il se termine après ou pendant le jour
                    return !pEnd.isBefore(startOfDay) && !pStart.isAfter(endOfDay);
                })
                .collect(Collectors.toList());
        
        // Extraire les IDs des techniciens qui travaillent ce jour
        List<Long> techniciensDisponiblesIds = planningsJour.stream()
                .map(p -> p.getUser().getId())
                .distinct()
                .collect(Collectors.toList());
        
        // Récupérer tous les techniciens et filtrer ceux qui ont un planning ce jour
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.TECHNICIEN_CURATIF || 
                             u.getRole() == UserRole.TECHNICIEN_PREVENTIF)
                .filter(u -> techniciensDisponiblesIds.contains(u.getId()))
                .collect(Collectors.toList());
    }
}