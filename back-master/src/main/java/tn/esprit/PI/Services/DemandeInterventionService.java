package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.Curative;
import tn.esprit.PI.entity.DemandeIntervention;
import tn.esprit.PI.entity.DemandeInterventionDTO;
import tn.esprit.PI.entity.Preventive;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.List;

@Service
public class DemandeInterventionService {
    @Autowired
    private DemandeInterventionRepository repository;

    @Autowired
    private UserRepository userRepository;

    // Ajouter une nouvelle demande


    // Récupérer une demande par ID
  /*  public Optional<DemandeInterventionDTO> getDemandeById(Long id) {
        return repository.findById(id).map(demande -> {
            String typeDemande;
            if (demande instanceof Curative) {
                typeDemande = "CURATIVE";
            } else if (demande instanceof Preventive) {
                typeDemande = "PREVENTIVE";
            } else {
                typeDemande = "INCONNU";
            }

            return new DemandeInterventionDTO(
                    demande.getId(),
                    demande.getDescription(),
                    demande.getDateDemande(),
                    demande.getStatut(),
                    demande.getPriorite(),
                    demande.getDemandeur() != null ? demande.getDemandeur().getId() : null,
                    typeDemande
            );
        });
    }*/


    // Récupérer toutes les demandes

    public List<DemandeInterventionDTO> getAllDemandes() {
        List<DemandeIntervention> demandes = repository.findAll();

        return demandes.stream().map(demande -> {
            DemandeInterventionDTO dto = new DemandeInterventionDTO();

            dto.setId(demande.getId());
            dto.setDescription(demande.getDescription());
            dto.setDateDemande(demande.getDateDemande());
            dto.setStatut(demande.getStatut());
            dto.setPriorite(demande.getPriorite());
            dto.setDemandeurId(demande.getDemandeur() != null ? demande.getDemandeur().getId() : null);
            dto.setTypeDemande(demande.getType_demande());

            // Si besoin d’afficher le prénom


            // Champs spécifiques à Curative
            if (demande instanceof Curative) {
                Curative curative = (Curative) demande;
                dto.setPanne(curative.getPanne());
                dto.setUrgence(curative.isUrgence());
            }

            // Champs spécifiques à Preventive
            if (demande instanceof Preventive) {
                Preventive preventive = (Preventive) demande;
                dto.setFrequence(preventive.getFrequence());
                dto.setProchainRDV(preventive.getProchainRDV());
            }

            return dto;
        }).toList();
    }



    // Mettre à jour une demande
    public DemandeInterventionDTO updateDemande(Long id, DemandeInterventionDTO dto) {
        return repository.findById(id).map(demande -> {
            // Mise à jour des champs fournis
            if (dto.getDescription() != null) {
                demande.setDescription(dto.getDescription());
            }
            if (dto.getDateDemande() != null) {
                demande.setDateDemande(dto.getDateDemande());
            }
            if (dto.getStatut() != null) {
                demande.setStatut(dto.getStatut());
            }
            if (dto.getPriorite() != null) {
                demande.setPriorite(dto.getPriorite());
            }
            if (dto.getDemandeurId() != null) {
                demande.setDemandeur(userRepository.findById(dto.getDemandeurId()).orElse(null));
            }
            
            DemandeIntervention updatedDemande = repository.save(demande);

            // Créer le DTO de retour
            DemandeInterventionDTO responseDto = new DemandeInterventionDTO();
            responseDto.setId(updatedDemande.getId());
            responseDto.setDescription(updatedDemande.getDescription());
            responseDto.setDateDemande(updatedDemande.getDateDemande());
            responseDto.setStatut(updatedDemande.getStatut());
            responseDto.setPriorite(updatedDemande.getPriorite());
            responseDto.setDemandeurId(updatedDemande.getDemandeur() != null ? updatedDemande.getDemandeur().getId() : null);
            responseDto.setTypeDemande(updatedDemande.getType_demande());

            return responseDto;

        }).orElseThrow(() -> new RuntimeException("Demande non trouvée"));
    }

    // Supprimer une demande
    public void deleteDemande(Long id) {
        repository.deleteById(id);
    }

}
