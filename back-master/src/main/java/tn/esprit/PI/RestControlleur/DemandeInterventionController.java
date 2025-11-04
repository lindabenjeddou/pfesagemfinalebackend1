package tn.esprit.PI.RestControlleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.DemandeInterventionService;
import tn.esprit.PI.Services.BonDeTravailService;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/demandes")
public class DemandeInterventionController {

    @Autowired
    private DemandeInterventionService demandeInterventionService;

    @Autowired
    private DemandeInterventionRepository demandeInterventionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BonDeTravailService bonDeTravailService;
    
    @Autowired
    private tn.esprit.PI.repository.TesteurRepository testeurRepository;

    /** ★ Interventions assignées à un technicien (DTO) */
    @GetMapping("/technicien/{techId}")
    public ResponseEntity<List<DemandeInterventionDTO>> getByTechnicien(@PathVariable Long techId) {
        return ResponseEntity.ok(demandeInterventionService.getByTechnicien(techId));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createIntervention(@RequestBody Map<String, Object> requestData) {
        try {
            String typeDemande = (String) requestData.get("type_demande");
            if (typeDemande == null) typeDemande = (String) requestData.get("type");
            if (typeDemande == null) typeDemande = (String) requestData.get("typeDemande");
            if (typeDemande != null) typeDemande = typeDemande.toUpperCase().trim();

            Long demandeurId = ((Number) requestData.get("demandeurId")).longValue();
            User demandeur = userRepository.findById(demandeurId)
                    .orElseThrow(() -> new RuntimeException("Demandeur non trouvé"));

            String description = (String) requestData.get("description");
            String priorite = (String) requestData.get("priorite");
            Date dateDemande = new Date();

            String statutStr = (String) requestData.get("statut");
            StatutDemande statut = StatutDemande.valueOf(statutStr.toUpperCase());

            // Récupérer le testeur si un code GMAO est fourni
            String testeurCodeGmao = (String) requestData.get("testeurCodeGmao");
            tn.esprit.PI.entity.Testeur testeur = null;
            if (testeurCodeGmao != null && !testeurCodeGmao.trim().isEmpty()) {
                testeur = testeurRepository.findById(testeurCodeGmao).orElse(null);
            }

            if ("CURATIVE".equals(typeDemande) || "CORRECTIVE".equals(typeDemande)) {
                Curative curative = new Curative();
                curative.setDescription(description);
                curative.setDateDemande(dateDemande);
                curative.setStatut(statut);
                curative.setPriorite(priorite);
                curative.setDemandeur(demandeur);
                curative.setTesteur(testeur);
                curative.setPanne((String) requestData.get("panne"));
                curative.setUrgence((Boolean) requestData.get("urgence"));
                
                DemandeIntervention saved = demandeInterventionRepository.save(curative);
                
                // Créer un DTO pour retourner avec les informations complètes
                DemandeInterventionDTO dto = new DemandeInterventionDTO();
                dto.setId(saved.getId());
                dto.setDescription(saved.getDescription());
                dto.setDateDemande(saved.getDateDemande());
                dto.setStatut(saved.getStatut());
                dto.setPriorite(saved.getPriorite());
                dto.setDemandeurId(demandeur.getId());
                dto.setTypeDemande(saved.getType_demande());
                dto.setTesteurCodeGMAO(testeur != null ? testeur.getCodeGMAO() : null);
                dto.setPanne(curative.getPanne());
                dto.setUrgence(curative.isUrgence());
                
                return ResponseEntity.ok(dto);
            } else if ("PREVENTIVE".equals(typeDemande)) {
                Preventive preventive = new Preventive();
                preventive.setDescription(description);
                preventive.setDateDemande(dateDemande);
                preventive.setStatut(statut);
                preventive.setPriorite(priorite);
                preventive.setDemandeur(demandeur);
                preventive.setTesteur(testeur);
                preventive.setFrequence((String) requestData.get("frequence"));
                preventive.setProchainRDV(new SimpleDateFormat("yyyy-MM-dd").parse((String) requestData.get("prochainRDV")));
                
                DemandeIntervention saved = demandeInterventionRepository.save(preventive);
                
                // Créer un DTO pour retourner avec les informations complètes
                DemandeInterventionDTO dto = new DemandeInterventionDTO();
                dto.setId(saved.getId());
                dto.setDescription(saved.getDescription());
                dto.setDateDemande(saved.getDateDemande());
                dto.setStatut(saved.getStatut());
                dto.setPriorite(saved.getPriorite());
                dto.setDemandeurId(demandeur.getId());
                dto.setTypeDemande(saved.getType_demande());
                dto.setTesteurCodeGMAO(testeur != null ? testeur.getCodeGMAO() : null);
                dto.setFrequence(preventive.getFrequence());
                dto.setProchainRDV(preventive.getProchainRDV());
                
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.badRequest()
                        .body("Type de demande non pris en charge. (CURATIVE / CORRECTIVE / PREVENTIVE)");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création: " + e.getMessage());
        }
    }

    @GetMapping("/recuperer/{id}")
    public ResponseEntity<DemandeInterventionDTO> getDemandeById(@PathVariable Long id) {
        Optional<DemandeInterventionDTO> demande = demandeInterventionService.getDemandeById(id);
        return demande.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/recuperer/all")
    public ResponseEntity<List<DemandeInterventionDTO>> getAllDemandes() {
        List<DemandeInterventionDTO> demandes = demandeInterventionService.getAllDemandes();
        return new ResponseEntity<>(demandes, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDemandesShort() {
        try {
            List<DemandeInterventionDTO> demandes = demandeInterventionService.getAllDemandes();
            return new ResponseEntity<>(demandes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des demandes: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDemande(@PathVariable Long id, @RequestBody DemandeInterventionDTO dto) {
        try {
            DemandeInterventionDTO updatedDemande = demandeInterventionService.updateDemande(id, dto);
            return new ResponseEntity<>(updatedDemande, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                    "error", "Erreur lors de la mise à jour",
                    "message", e.getMessage()
            ), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                    "error", "Erreur interne du serveur",
                    "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/assign/{interventionId}/technicien/{technicienId}")
    public ResponseEntity<?> assignTechnicianToIntervention(
            @PathVariable Long interventionId,
            @PathVariable Long technicienId) {
        try {
            DemandeInterventionDTO updated = demandeInterventionService
                    .assignTechnicianToIntervention(interventionId, technicienId);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                    "error", "Erreur lors de l'affectation",
                    "message", e.getMessage()
            ), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                    "error", "Erreur interne du serveur",
                    "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/assign/{interventionId}/testeur/{testeurCodeGMAO}")
    public ResponseEntity<?> assignTesteurToIntervention(
            @PathVariable Long interventionId,
            @PathVariable String testeurCodeGMAO) {
        try {
            DemandeInterventionDTO updated = demandeInterventionService
                    .assignTesteurToIntervention(interventionId, testeurCodeGMAO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                    "error", "Erreur lors de l'affectation du testeur",
                    "message", e.getMessage()
            ), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                    "error", "Erreur interne du serveur",
                    "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/confirmer/{interventionId}")
    public ResponseEntity<?> confirmerIntervention(@PathVariable Long interventionId) {
        try {
            DemandeInterventionDTO updated = demandeInterventionService
                    .confirmerIntervention(interventionId);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                    "error", "Erreur lors de la confirmation",
                    "message", e.getMessage()
            ), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                    "error", "Erreur interne du serveur",
                    "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* ---- Bons de travail liés à l’intervention (si nécessaire ici) ---- */

    @PostMapping("/{interventionId}/bon-travail/technicien/{technicienId}")
    public ResponseEntity<?> createBonDeTravailForIntervention(
            @PathVariable Long interventionId,
            @PathVariable Long technicienId,
            @RequestBody BonTravailRequest request) {
        try {
            BonDeTravail bon = bonDeTravailService
                    .createBonDeTravailFromIntervention(interventionId, technicienId, request);
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

    @GetMapping("/{interventionId}/bons-travail")
    public ResponseEntity<?> getBonsDeTravailForIntervention(@PathVariable Long interventionId) {
        try {
            List<BonDeTravail> bons = bonDeTravailService.getBonsDeTravailByIntervention(interventionId);
            return new ResponseEntity<>(bons, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(
                    "error", "Erreur lors de la récupération des bons de travail",
                    "message", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
