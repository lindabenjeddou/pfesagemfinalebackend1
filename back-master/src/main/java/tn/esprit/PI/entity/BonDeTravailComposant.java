package tn.esprit.PI.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "bon_de_travail_composant",
        uniqueConstraints = @UniqueConstraint(columnNames = {"bon_id", "trart_article"}))

public class BonDeTravailComposant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bon_id")
    BonDeTravail bon;

    // NOTE: on référence la colonne primaire TRART_ARTICLE
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trart_article", referencedColumnName = "TRART_ARTICLE")
    Component composant;

    @Column(name = "quantite_utilisee", precision = 12, scale = 3, nullable = false)
    BigDecimal quantiteUtilisee;
}
