package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.Planing;
import tn.esprit.PI.repository.PlaningRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlaningService {
    private final PlaningRepository planingRepository;

    @Autowired
    public PlaningService(PlaningRepository planingRepository) {
        this.planingRepository = planingRepository;
    }

    // Créer un nouveau planning
    public Planing createPlaning(Planing planing) {
        if (planing.getStartDate() == null) {
            planing.setStartDate(LocalDateTime.now());  // Par défaut, on prend la date actuelle pour le début
        }
        if (planing.getEndDate() == null) {
            planing.setEndDate(planing.getStartDate().plusHours(1));  // Par défaut, on suppose que la tâche dure 1 heure
        }
        return planingRepository.save(planing);
    }

    // Récupérer tous les plannings
    public List<Planing> getAllPlannings() {
        return planingRepository.findAll();
    }

    // Récupérer un planning par son ID
    public Optional<Planing> getPlaningById(Long id) {
        return planingRepository.findById(id);
    }

    // Mettre à jour un planning existant
    public Planing updatePlaning(Long id, Planing updatedPlaning) {
        Optional<Planing> existingPlaning = planingRepository.findById(id);
        if (existingPlaning.isPresent()) {
            Planing planing = existingPlaning.get();
            planing.setStartDate(updatedPlaning.getStartDate() != null ? updatedPlaning.getStartDate() : planing.getStartDate());
            planing.setEndDate(updatedPlaning.getEndDate() != null ? updatedPlaning.getEndDate() : planing.getEndDate());
            planing.setStatus(updatedPlaning.getStatus() != null ? updatedPlaning.getStatus() : planing.getStatus());
            planing.setTaskDescription(updatedPlaning.getTaskDescription() != null ? updatedPlaning.getTaskDescription() : planing.getTaskDescription());
            planing.setLocation(updatedPlaning.getLocation() != null ? updatedPlaning.getLocation() : planing.getLocation());
            planing.setComments(updatedPlaning.getComments() != null ? updatedPlaning.getComments() : planing.getComments());
            planing.setIsUrgent(updatedPlaning.getIsUrgent() != null ? updatedPlaning.getIsUrgent() : planing.getIsUrgent());
            planing.setPriority(updatedPlaning.getPriority() != null ? updatedPlaning.getPriority() : planing.getPriority());

            return planingRepository.save(planing);
        }
        return null;
    }

    // Supprimer un planning par son ID
    public boolean deletePlaning(Long id) {
        Optional<Planing> existingPlaning = planingRepository.findById(id);
        if (existingPlaning.isPresent()) {
            planingRepository.delete(existingPlaning.get());
            return true;
        }
        return false;
    }

    // Récupérer les plannings d'un utilisateur (par exemple, un technicien)
    public List<Planing> getPlanningsByUserId(Long userId) {
        return planingRepository.findByUserId(userId);
    }

    // Vérifier si un technicien a un planning pour un jour donné
    public boolean isTechnicianAvailable(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Planing> plannings = planingRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        return plannings.isEmpty();
    }

}
