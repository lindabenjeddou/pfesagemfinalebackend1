package tn.esprit.PI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BonDeTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String description;

    @CreationTimestamp
    @Column(nullable = false)
    LocalDate dateCreation;

    LocalDate dateDebut;
    LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatutBonTravail statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler",
            "sousProjets", "password", "token", "resetToken"
    })
    User technicien;

    /** IMPORTANT : on coupe ici la remontée vers l'intervention pour éviter la sérialisation des proxys LAZY. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id")
    @JsonIgnore
    DemandeIntervention intervention;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testeur_code_gmao")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    Testeur testeur;

    @OneToMany(mappedBy = "bon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<BonTravailComponent> composants;

    // Si tu gardes un champ brut du code GMAO en plus de l'entité Testeur
    // String testeurCodeGMAO;
}
