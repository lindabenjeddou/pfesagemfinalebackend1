package tn.esprit.PI.RestControlleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.DemandeInterventionService;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/PI/demandes")
public class DemandeInterventionController {

    @Autowired
    private DemandeInterventionService demandeInterventionService;

    @Autowired
    private DemandeInterventionRepository demandeInterventionRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createIntervention(@RequestBody Map<String, Object> requestData) {
        try {
            String typeDemande = (String) requestData.get("type_demande");
            Long demandeurId = ((Number) requestData.get("demandeurId")).longValue();
            User demandeur = userRepository.findById(demandeurId)
                    .orElseThrow(() -> new RuntimeException("Demandeur non trouvé"));

            String description = (String) requestData.get("description");
            String priorite = (String) requestData.get("priorite");
            Date dateDemande = new Date(); // ou convertis à partir de requestData si besoin

            // Récupérer le statut de la demande depuis la requête
            String statutStr = (String) requestData.get("statut");
            StatutDemande statut = StatutDemande.valueOf(statutStr.toUpperCase()); // Conversion du statut en valeur de l'énumération

            if ("CURATIVE".equals(typeDemande)) {
                Curative curative = new Curative();
                curative.setDescription(description);
                curative.setDateDemande(dateDemande);
                curative.setStatut(statut); // Appliquer le statut fourni
                curative.setPriorite(priorite);
                curative.setDemandeur(demandeur);
                curative.setPanne((String) requestData.get("panne"));
                curative.setUrgence((Boolean) requestData.get("urgence"));
                return ResponseEntity.ok(demandeInterventionRepository.save(curative));
            } else if ("PREVENTIVE".equals(typeDemande)) {
                Preventive preventive = new Preventive();
                preventive.setDescription(description);
                preventive.setDateDemande(dateDemande);
                preventive.setStatut(statut); // Appliquer le statut fourni
                preventive.setPriorite(priorite);
                preventive.setDemandeur(demandeur);
                preventive.setFrequence((String) requestData.get("frequence"));
                preventive.setProchainRDV(new SimpleDateFormat("yyyy-MM-dd").parse((String) requestData.get("prochainRDV")));
                return ResponseEntity.ok(demandeInterventionRepository.save(preventive));
            } else {
                return ResponseEntity.badRequest().body("Type de demande non pris en charge.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création");
        }
    }

/*
    @GetMapping("/recuperer/{id}")
    public ResponseEntity<DemandeInterventionDTO> getDemandeById(@PathVariable Long id) {
        Optional<DemandeInterventionDTO> demande = demandeInterventionService.getDemandeById(id);
        return demande.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }*/

    @GetMapping("/recuperer/all")
    public ResponseEntity<List<DemandeInterventionDTO>> getAllDemandes() {
        List<DemandeInterventionDTO> demandes = demandeInterventionService.getAllDemandes();
        return new ResponseEntity<>(demandes, HttpStatus.OK);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<DemandeInterventionDTO> updateDemande(@PathVariable Long id, @RequestBody DemandeInterventionDTO dto) {
        try {
            DemandeInterventionDTO updatedDemande = demandeInterventionService.updateDemande(id, dto);
            return new ResponseEntity<>(updatedDemande, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDemande(@PathVariable Long id) {
        demandeInterventionService.deleteDemande(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
