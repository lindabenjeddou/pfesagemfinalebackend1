package tn.esprit.PI.RestControlleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.PlanningHoraireService;
import tn.esprit.PI.entity.PlanningHoraire;
import tn.esprit.PI.entity.PlanningHoraireDTO;
import tn.esprit.PI.entity.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    /**
     * Endpoint pour récupérer les techniciens disponibles à une date donnée
     * URL: /PI/planningHoraire/techniciens-disponibles?date=2025-11-02
     */
    @GetMapping("/techniciens-disponibles")
    public ResponseEntity<?> getTechniciensDisponibles(@RequestParam String date) {
        try {
            // Parser la date (format attendu: yyyy-MM-dd)
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            
            // Récupérer les techniciens disponibles
            List<User> techniciensDisponibles = planningHoraireService.getTechniciensDisponibles(localDate);
            
            // Créer un DTO simple pour la réponse
            List<Map<String, Object>> techniciensDTOs = techniciensDisponibles.stream()
                    .map(t -> {
                        Map<String, Object> techMap = new java.util.HashMap<>();
                        techMap.put("id", t.getId());
                        techMap.put("firstName", t.getFirstname() != null ? t.getFirstname() : "");
                        techMap.put("lastName", t.getLastname() != null ? t.getLastname() : "");
                        techMap.put("role", t.getRole().name());
                        techMap.put("email", t.getEmail() != null ? t.getEmail() : "");
                        return techMap;
                    })
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("date", date);
            response.put("nombreDisponibles", techniciensDTOs.size());
            response.put("techniciens", techniciensDTOs);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Erreur lors de la récupération des techniciens disponibles");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
