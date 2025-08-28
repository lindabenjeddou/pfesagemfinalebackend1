package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.PlanningHoraire;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.repository.PlanningHoraireRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.List;
import java.util.Optional;

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
}
