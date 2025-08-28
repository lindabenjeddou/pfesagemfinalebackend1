package tn.esprit.PI.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProjetDTO {
    private String nomProjet;
    private String nomChefProjet;
    private String description;
    private List<String> components;
    private Date date;
    private float budget;
}
