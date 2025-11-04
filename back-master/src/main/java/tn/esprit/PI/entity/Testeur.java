package tn.esprit.PI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "testeurs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Testeur implements Serializable {

    @Id
    @Column(name = "code_GMAO", nullable = false, unique = true)
    private String codeGMAO;

    @Column(name = "atelier", nullable = false)
    private String atelier;

    @Column(name = "ligne", nullable = false)
    private String ligne;

    @Column(name = "banc_de_Test", nullable = false)
    private String bancTest;

    // Relation One-to-Many avec DemandeIntervention
    // Un testeur (équipement) peut avoir plusieurs interventions
    @OneToMany(mappedBy = "testeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DemandeIntervention> interventions;
}
