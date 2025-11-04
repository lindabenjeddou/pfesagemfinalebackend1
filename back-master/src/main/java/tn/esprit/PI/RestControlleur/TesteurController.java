package tn.esprit.PI.RestControlleur;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.TesteurService;
import tn.esprit.PI.entity.Testeur;
import tn.esprit.PI.entity.TesteurDTO;

import java.util.List;

@RestController
@RequestMapping("/PI/testeurs")
public class TesteurController {

    @Autowired
    private TesteurService testeurService;

    // Créer un testeur
    @PostMapping("/create")
    public ResponseEntity<Testeur> createTesteur(@RequestBody Testeur testeur) {
        Testeur createdTesteur = testeurService.createTesteur(testeur);
        return ResponseEntity.ok(createdTesteur);
    }

    // Récupérer tous les testeurs (avec DTO pour éviter sérialisation circulaire)
    @GetMapping("/all")
    public ResponseEntity<List<TesteurDTO>> getAllTesteurs() {
        try {
            List<TesteurDTO> testeurs = testeurService.getAllTesteursDTO();
            return ResponseEntity.ok(testeurs);
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la récupération des testeurs: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Mettre à jour un testeur
    @PutMapping("/update/{atelier}/{ligne}")
    public ResponseEntity<Testeur> updateTesteur(
            @PathVariable String atelier,
            @PathVariable String ligne,
            @RequestBody Testeur testeurDetails) {
        Testeur updatedTesteur = testeurService.updateTesteur(atelier, ligne, testeurDetails);
        if (updatedTesteur != null) {
            return ResponseEntity.ok(updatedTesteur);
        }
        return ResponseEntity.notFound().build();
    }

    // Supprimer un testeur
    @DeleteMapping("/delete/{atelier}/{ligne}")
    public ResponseEntity<Void> deleteTesteur(
            @PathVariable String atelier,
            @PathVariable String ligne) {
        testeurService.deleteTesteur(atelier, ligne);
        return ResponseEntity.noContent().build();
    }
}