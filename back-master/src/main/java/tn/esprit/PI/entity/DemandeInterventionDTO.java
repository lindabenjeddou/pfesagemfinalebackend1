package tn.esprit.PI.entity;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DemandeInterventionDTO {
    private Long id;
    private String description;
    private Date dateDemande;
    private StatutDemande statut;
    private Long demandeurId; // On envoie uniquement l'ID de l'utilisateur
    private String priorite;
    private String typeDemande; // "CURATIVE" ou "PREVENTIVE"
    private String panne;
    private Boolean urgence;
    private String frequence;
    private Date prochainRDV;
    
    // Additional fields to match entity
    private Date dateCreation;
    private Date dateValidation;
    private Integer confirmation;
    private String testeurCodeGMAO;
    private Long technicienAssigneId;

    // Constructeur avec tous les paramètres
    public DemandeInterventionDTO(Long id, String description, Date dateDemande, StatutDemande statut,
                                  String priorite, Long demandeurId, String typeDemande,
                                  String panne, Boolean urgence, String frequence, Date prochainRDV) {
        this.id = id;
        this.description = description;
        this.dateDemande = dateDemande;
        this.statut = statut;
        this.priorite = priorite;
        this.demandeurId = demandeurId;
        this.typeDemande = typeDemande;
        this.panne = panne;
        this.urgence = urgence;
        this.frequence = frequence;
        this.prochainRDV = prochainRDV;
    }


}
