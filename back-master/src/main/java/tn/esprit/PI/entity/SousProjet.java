package tn.esprit.PI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SousProjet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sous_projet_name")
    private String sousProjetName;

    @Column(name = "description")
    private String description;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;


    @Column(name = "confirmed", nullable = false)
    private Integer confirmed =0 ; // Default value is 0 (not confirmed)

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany
    @JoinTable(
            name = "sous_projet_articles",
            joinColumns = @JoinColumn(name = "sous_projet_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Component> components = new ArrayList<>();

    @ManyToMany(mappedBy = "sousProjets")
    @JsonIgnore
    private List<User> users;
}
