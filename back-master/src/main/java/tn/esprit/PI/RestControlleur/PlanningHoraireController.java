package tn.esprit.PI.RestControlleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.PlanningHoraireService;
import tn.esprit.PI.entity.PlanningHoraire;
import tn.esprit.PI.entity.PlanningHoraireDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/PI/planningHoraire")
public class PlanningHoraireController {

    @Autowired
    private PlanningHoraireService planningHoraireService;

    @PostMapping("/add")
    public ResponseEntity<?> addPlanningHoraire(@RequestBody PlanningHoraire planningHoraire) {
        try {
            PlanningHoraire saved = planningHoraireService.savePlanningHoraire(planningHoraire);
            return ResponseEntity.ok(Map.of(
                    "message", "Planning ajouté avec succès",
                    "planning", saved
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePlanningHoraire(@PathVariable Long id, @RequestBody PlanningHoraire planningHoraire) {
        try {
            Optional<PlanningHoraire> existing = planningHoraireService.findById(id);
            if (existing.isPresent()) {
                planningHoraire.setId(id);
                PlanningHoraire updated = planningHoraireService.savePlanningHoraire(planningHoraire);
                return ResponseEntity.ok(Map.of(
                        "message", "Planning mis à jour avec succès",
                        "planning", updated
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePlanningHoraire(@PathVariable Long id) {
        try {
            Optional<PlanningHoraire> existing = planningHoraireService.findById(id);
            if (existing.isPresent()) {
                planningHoraireService.deletePlanningHoraire(id);
                return ResponseEntity.ok(Map.of("message", "Planning supprimé avec succès"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPlanningHoraires() {
        try {
            List<PlanningHoraire> plannings = planningHoraireService.findAllPlanningHoraires();

            List<PlanningHoraireDTO> dtoList = plannings.stream()
                    .filter(p -> p.getUser() != null)
                    .map(p -> PlanningHoraireDTO.builder()
                            .id(p.getId())
                            .userId(p.getUser().getId())
                            .firstName(p.getUser().getFirstname())
                            .lastName(p.getUser().getLastname())
                            .role(p.getUser().getRole().name())
                            .description(p.getDescription())
                            .startDate(p.getStartDate().toString())
                            .endDate(p.getEndDate().toString())
                            .valid(p.getValid())
                            .build())
                    .toList();

            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }
}
