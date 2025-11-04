package tn.esprit.PI.entity;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TesteurDTO {
    private String codeGMAO;
    private String atelier;
    private String ligne;
    private String bancTest;
    private List<Long> interventionIds; // Seulement les IDs pour éviter la sérialisation circulaire
    private int nombreInterventions; // Nombre total d'interventions
}
