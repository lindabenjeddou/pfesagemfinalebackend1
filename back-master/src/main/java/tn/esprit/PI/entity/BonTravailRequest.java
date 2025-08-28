package tn.esprit.PI.entity;

import java.time.LocalDate;
import java.util.List;

public class BonTravailRequest {

    public String description;
    public LocalDate dateCreation;
    public LocalDate dateDebut;
    public LocalDate dateFin;
    public StatutBonTravail statut;
    public Long technicien; // technicienId
    public List<String> composants; // ids
}
