package tn.esprit.PI.RestControlleur;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;

import tn.esprit.PI.entity.Project;
import tn.esprit.PI.entity.ProjetDTO;
import tn.esprit.PI.entity.SousProjet;
import tn.esprit.PI.Services.SousProjetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.entity.SousProjetDto;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/PI/sousprojets")
public class SousProjetController {

        @Autowired
        private SousProjetService sousProjetService;

        // Create a new SousProjet

    @PostMapping("/create/{projectId}")
    public ResponseEntity<?> createSousProjet(
            @RequestBody SousProjetDto sousProjetDto,
            @PathVariable Long projectId) {
        try {
            SousProjet createdSousProjet = sousProjetService.createSousProjet(sousProjetDto, projectId);
            return ResponseEntity.ok(createdSousProjet);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred.");
        }
    }

    // Get all SousProjets
        @GetMapping("/")
        public ResponseEntity<List<SousProjetDto>> getAllSousProjets() {
            List<SousProjetDto> sousProjets = sousProjetService.getAllSousProjets();
            return ResponseEntity.ok(sousProjets);
        }

        // Get SousProjets by Project ID
        @GetMapping("/project/{projectId}")
        public ResponseEntity<List<SousProjet>> getSousProjetsByProjectId(@PathVariable Long projectId) {
            List<SousProjet> sousProjets = sousProjetService.getSousProjetsByProjectId(projectId);
            return ResponseEntity.ok(sousProjets);
        }


    @GetMapping("sousprojet/{id}")
    public ResponseEntity<SousProjet> getSousProjetById(@PathVariable Long id) {
        SousProjet sousProjet = sousProjetService.getSousProjetById(id);
        return ResponseEntity.ok(sousProjet);
    }
        // Update an existing SousProjet
        @PutMapping("/update/{id}")
        public ResponseEntity<SousProjet> updateSousProjet(
                @PathVariable Long id,
                @RequestBody SousProjetDto sousProjetDto) {
            SousProjet updatedSousProjet = sousProjetService.updateSousProjet(id, sousProjetDto);
            return ResponseEntity.ok(updatedSousProjet);
        }

        // Delete a SousProjet
        @DeleteMapping("/delete/{id}")
        public ResponseEntity<Void> deleteSousProjet(@PathVariable Long id) {
            sousProjetService.deleteSousProjet(id);
            return ResponseEntity.noContent().build();
        }



    @PutMapping("/confirm/{id}")
    public ResponseEntity<SousProjet> confirmSousProjetAutomatically(@PathVariable Long id) {
        SousProjet confirmedSousProjet = sousProjetService.confirmSousProjetAutomatically(id);
        return ResponseEntity.ok(confirmedSousProjet);
    }
    }
