package tn.esprit.PI.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_demande", discriminatorType = DiscriminatorType.STRING)
public class DemandeIntervention implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(insertable = false, updatable = false)
    private String type_demande;

    private String description;
    private Date dateDemande;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    private String priorite;

    // Additional fields based on system requirements
    @CreationTimestamp
    private Date dateCreation;
    
    private Date dateValidation;
    
    private Integer confirmation = 0; // 0 = non confirmé, 1 = confirmé

    // Relationships
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demandeur")
    @JsonBackReference
    private User demandeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testeur_code_gmao")
    @JsonBackReference
    private Testeur testeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technicien_id")
    private User technicienAssigne;

}

