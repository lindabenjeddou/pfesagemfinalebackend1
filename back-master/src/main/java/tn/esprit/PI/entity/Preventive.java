package tn.esprit.PI.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@DiscriminatorValue("PREVENTIVE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
 public  class Preventive extends DemandeIntervention {
    private String frequence;
    private Date prochainRDV;

    public void planifierIntervention() {
        System.out.println("Intervention préventive planifiée pour le: " + prochainRDV);
    }
}
