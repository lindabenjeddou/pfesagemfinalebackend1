package tn.esprit.PI.RestControlleur;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.PlaningService;
import tn.esprit.PI.entity.Planing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
@RequestMapping("/PI/planing")
public class PlaningController {
    private final PlaningService planingService;

    // Créer un nouveau planning
    @PostMapping("/create")
    public ResponseEntity<Planing> createPlaning(@RequestBody Planing planing) {
        Planing createdPlaning = planingService.createPlaning(planing);
        return new ResponseEntity<>(createdPlaning, HttpStatus.CREATED);
    }

    // Récupérer tous les plannings
    @GetMapping("/recuperer/all")
    public ResponseEntity<List<Planing>> getAllPlannings() {
        List<Planing> plannings = planingService.getAllPlannings();
        return new ResponseEntity<>(plannings, HttpStatus.OK);
    }

    // Récupérer un planning par son ID
    @GetMapping("/recuperer/{id}")
    public ResponseEntity<Planing> getPlaningById(@PathVariable Long id) {
        Optional<Planing> planing = planingService.getPlaningById(id);
        return planing.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Mettre à jour un planning
    @PutMapping("/update/{id}")
    public ResponseEntity<Planing> updatePlaning(@PathVariable Long id, @RequestBody Planing updatedPlaning) {
        Planing planing = planingService.updatePlaning(id, updatedPlaning);
        return planing != null ? new ResponseEntity<>(planing, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Supprimer un planning
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePlaning(@PathVariable Long id) {
        boolean isDeleted = planingService.deletePlaning(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Récupérer les plannings d'un utilisateur (technicien)
    @GetMapping("/recuperer/user/{userId}")
    public ResponseEntity<List<Planing>> getPlanningsByUserId(@PathVariable Long userId) {
        List<Planing> plannings = planingService.getPlanningsByUserId(userId);
        return new ResponseEntity<>(plannings, HttpStatus.OK);
    }

    // Vérifier la disponibilité d'un technicien pour une plage horaire donnée
    @GetMapping("/check-availability/{userId}")
    public ResponseEntity<Boolean> checkTechnicianAvailability(@PathVariable Long userId,
                                                               @RequestParam("startDate") LocalDateTime startDate,
                                                               @RequestParam("endDate") LocalDateTime endDate) {
        boolean isAvailable = planingService.isTechnicianAvailable(userId, startDate, endDate);
        return new ResponseEntity<>(isAvailable, HttpStatus.OK);
    }
}
