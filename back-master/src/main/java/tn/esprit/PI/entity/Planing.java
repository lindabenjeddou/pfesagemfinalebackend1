package tn.esprit.PI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Planing implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // L'utilisateur associé au planning

    @Column(nullable = false)
    private LocalDateTime startDate;  // Date de début de la tâche

    @Column(nullable = false)
    private LocalDateTime endDate;  // Date de fin de la tâche

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanningStatus status;  // Statut de la tâche (par exemple, "En cours", "Terminé", etc.)

    @Column(length = 500)
    private String taskDescription;  // Description de la tâche assignée à l'utilisateur

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Location location;  // L'emplacement de l'erreur

    @Column
    private String comments;  // Commentaires supplémentaires sur la tâche

    @Column
    private Boolean isUrgent;  // Indicateur si la tâche est urgente (optionnel)

    @Column
    private String priority;  // Niveau de priorité de la tâche (par exemple, "Haute", "Moyenne", "Basse")
    @Column
    private LocalDateTime dateRange;  //

    @PrePersist
    public void prePersist() {
        if (startDate == null) {
            startDate = LocalDateTime.now();  // Par défaut, la date de début est maintenant
        }
        if (endDate == null) {
            endDate = startDate.plusHours(1);  // Par défaut, on suppose que la tâche dure 1 heure
        }
    }

    public boolean isTechnician() {
        // Vérifie si l'utilisateur est un technicien
        return user != null &&
                (user.getRole() == UserRole.TECHNICIEN_CURATIF || user.getRole() == UserRole.TECHNICIEN_PREVENTIF);
    }


}
