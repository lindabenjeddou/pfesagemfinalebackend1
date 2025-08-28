package tn.esprit.PI.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "testeurs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Testeur implements Serializable {

    @Id
    @Column(name = "code_GMAO", nullable = false, unique = true)
    private String codeGMAO;

    @Column(name = "atelier", nullable = false)
    private String atelier;

    @Column(name = "ligne", nullable = false)
    private String ligne;

    @Column(name = "banc_de_test", nullable = false)
    private String bancTest;
}
