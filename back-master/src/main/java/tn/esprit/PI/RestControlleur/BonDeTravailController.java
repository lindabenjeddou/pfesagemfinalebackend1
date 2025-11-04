package tn.esprit.PI.RestControlleur;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.BonDeTravailService;
import tn.esprit.PI.entity.BonDeTravail;
import tn.esprit.PI.entity.BonTravailRequest;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/pi/bons")
@RequiredArgsConstructor
public class BonDeTravailController {

    private final BonDeTravailService bonService;

    @GetMapping
    public List<BonDeTravail> getAll() {
        return bonService.getAllBonDeTravail();
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.of(
            "message", "Contrôleur BonDeTravail fonctionne",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/{id}")
    public BonDeTravail getById(@PathVariable Long id) {
        return bonService.getBonDeTravailById(id);
    }

    @PostMapping
    public BonDeTravail create(@RequestBody BonTravailRequest request) {
        return bonService.createBonDeTravail(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BonTravailRequest request) {
        System.out.println("=== UPDATE ENDPOINT CALLED ===");
        System.out.println("ID reçu: " + id);
        System.out.println("Request body: " + (request != null ? "non null" : "null"));

        if (request != null) {
            System.out.println("Description: " + request.description);
            System.out.println("Technicien ID: " + request.technicien);
            if (request.composants != null) {
                System.out.println("Nombre de composants: " + request.composants.size());
                for (int i = 0; i < request.composants.size(); i++) {
                    var comp = request.composants.get(i);
                    System.out.println("  Composant " + i + ": ID=" + comp.id + ", Quantité=" + comp.quantite);
                }
            } else {
                System.out.println("Aucun composant dans la requête");
            }
        }

        try {
            // Validation de l'ID
            if (id == null || id <= 0) {
                System.out.println("Erreur: ID invalide - " + id);
                return new ResponseEntity<>(Map.of(
                    "error", "ID invalide",
                    "message", "L'ID du bon de travail doit être un nombre positif"
                ), HttpStatus.BAD_REQUEST);
            }

            // Validation du request body
            if (request == null) {
                return new ResponseEntity<>(Map.of(
                    "error", "Données manquantes",
                    "message", "Le corps de la requête ne peut pas être vide"
                ), HttpStatus.BAD_REQUEST);
            }

            BonDeTravail updatedBon = bonService.updateBonDeTravail(id, request);
            return new ResponseEntity<>(updatedBon, HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de la mise à jour du bon de travail",
                "message", e.getMessage(),
                "id", id
            ), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur interne du serveur",
                "message", e.getMessage(),
                "id", id
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            bonService.deleteBonDeTravail(id);
            return new ResponseEntity<>(Map.of(
                "message", "Bon de travail supprimé avec succès",
                "id", id
            ), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de la suppression",
                "message", e.getMessage()
            ), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur interne du serveur",
                "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Créer un bon de travail basé sur une intervention
    @PostMapping("/intervention/{interventionId}/technicien/{technicienId}")
    public ResponseEntity<?> createFromIntervention(
            @PathVariable Long interventionId,
            @PathVariable Long technicienId,
            @RequestBody BonTravailRequest request) {
        try {
            BonDeTravail bon = bonService.createBonDeTravailFromIntervention(interventionId, technicienId, request);
            return new ResponseEntity<>(bon, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de la création du bon de travail",
                "message", e.getMessage()
            ), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur interne du serveur",
                "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer tous les bons de travail d'une intervention
    @GetMapping("/intervention/{interventionId}")
    public ResponseEntity<?> getByIntervention(@PathVariable Long interventionId) {
        try {
            List<BonDeTravail> bons = bonService.getBonsDeTravailByIntervention(interventionId);
            return new ResponseEntity<>(bons, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de la récupération des bons de travail",
                "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer tous les bons de travail d'un testeur (équipement)
    @GetMapping("/testeur/{testeurCodeGMAO}")
    public ResponseEntity<?> getByTesteur(@PathVariable String testeurCodeGMAO) {
        try {
            List<BonDeTravail> bons = bonService.getBonsDeTravailByTesteur(testeurCodeGMAO);
            return new ResponseEntity<>(bons, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                "error", "Erreur lors de la récupération des bons de travail",
                "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

