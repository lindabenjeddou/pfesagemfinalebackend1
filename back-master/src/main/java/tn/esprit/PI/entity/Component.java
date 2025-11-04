package tn.esprit.PI.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Component implements Serializable {
    @Id
    @Column(name = "TRART_ARTICLE")
    private String trartArticle;

    @Column(name = "ATL")
    private String atl;

    @Column(name = "BUR")
    private String bur;

    @Column(name = "TRART_DESIGNATION")
    private String trartDesignation;

    @Column(name = "TRART_FAMILLE")
    private String trartFamille;

    @Column(name = "TRART_MARQUE")
    private String trartMarque;

    @Column(name = "TRART_QUANTITE")
    private String trartQuantite;

    @Column(name = "TRART_SOCIETE")
    private String trartSociete;

    @Column(name = "TRART_SOUS_FAMILLE")
    private String trartSousFamille;

    @Column(name = "TRART_TRANSACT")
    private String trartTransact;

    @Column(name = "TRART_TVA")
    private String trartTva;

    @Column(name = "TEST")
    private String test;

    @Column(name = "TRART_UNITE")
    private String trartUnite;

    @Column(name = "Prix")
    private String Prix;




    @PrePersist
    public void prePersist() {
        if (this.trartArticle == null) {
            this.trartArticle = UUID.randomUUID().toString();  // Génère un identifiant unique
        }
    }


}
