package tn.esprit.PI.entity;


import tn.esprit.PI.entity.StatutBonTravail;
import tn.esprit.PI.entity.User;

import java.time.LocalDate;
import java.util.List;

public class BonTravailResponse {
    public Long id;
    public String description;
    public LocalDate dateCreation;
    public LocalDate dateDebut;
    public LocalDate dateFin;
    public StatutBonTravail statut;
    public Long technicien;// ou juste l’id si tu préfères
    public List<ComposantQuantite> composants;

    public static class ComposantQuantite {
        public String id;
        public String designation;
        public int quantiteUtilisee;
    }
}
