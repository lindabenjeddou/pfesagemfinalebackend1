package tn.esprit.PI.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CURATIVE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Curative extends DemandeIntervention {

    private String panne;
    private boolean urgence;

    public void genererPanne() {
        System.out.println("Génération d'une intervention pour panne: " + panne);
    }
}
