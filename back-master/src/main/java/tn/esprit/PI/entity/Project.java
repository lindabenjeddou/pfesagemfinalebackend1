package tn.esprit.PI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "project_manager_name", nullable = false)
    private String projectManagerName;


    @Column(name = "description")
    private String description;

    @Column(name = "budget")
    private Float budget;

    @Column(name = "date")
    private Date date;

    @ManyToMany
    @JoinTable(
            name = "project_components",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "component_trart_article")
    )
    private List<Component> components = new ArrayList<>();

    @JsonIgnore  // Prevent recursive serialization of sousProjets
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<SousProjet> sousProjets = new ArrayList<>();
}
