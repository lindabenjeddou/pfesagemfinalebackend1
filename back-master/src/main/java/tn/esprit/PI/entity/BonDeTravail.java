package tn.esprit.PI.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Column(nullable = false)
    LocalDate dateCreation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatutBonTravail statut;

    @ManyToOne
    @JoinColumn(name = "technicien_id", nullable = false)
    User technicien;


    @ManyToMany
    @JoinTable(
            name = "bon_travail_composants",
            joinColumns = @JoinColumn(name = "bon_id"),
            inverseJoinColumns = @JoinColumn(name = "trart_article")
    )
    List<Component> composants;


}

// Enumération des statuts du Bon de Travail



