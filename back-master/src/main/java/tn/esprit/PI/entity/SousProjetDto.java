package tn.esprit.PI.entity;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Getter
@Setter
public class SousProjetDto {
    private Long id;
    private Long projectId;
    private String sousProjetName;
    private String description;
    private List<String> components; // Liste des IDs de composants
    private List<Long> users;

    private Double totalPrice;


    private Integer confirmed;

}
