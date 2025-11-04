package tn.esprit.PI.mapper;

import tn.esprit.PI.entity.BonDeTravail;
import tn.esprit.PI.entity.BonTravailComponent;
import tn.esprit.PI.entity.BonTravailResponse;


import java.util.List;

public class BonTravailMapper {

    public static BonTravailResponse mapToDto(BonDeTravail bon) {
        BonTravailResponse dto = new BonTravailResponse();
        dto.id = bon.getId();
        dto.description = bon.getDescription();
        dto.dateCreation = bon.getDateCreation();
        dto.dateDebut = bon.getDateDebut();
        dto.dateFin = bon.getDateFin();
        dto.statut = bon.getStatut();
        dto.technicien = bon.getTechnicien().getId(); // ✅ bon.getTechnicien() est un User
// ou getTechnicien().getId() si DTO simplifié

        dto.composants = bon.getComposants().stream().map(btc -> {
            BonTravailResponse.ComposantQuantite comp = new BonTravailResponse.ComposantQuantite();
            comp.id = btc.getComponent().getTrartArticle();
            comp.designation = btc.getComponent().getTrartDesignation();
            comp.quantiteUtilisee = btc.getQuantiteUtilisee();
            return comp;
        }).toList();

        return dto;
    }
}
