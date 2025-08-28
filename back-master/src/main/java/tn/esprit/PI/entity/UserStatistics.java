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
@Table(name = "user_statistics")
public class UserStatistics implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_interventions")
    private Integer totalInterventions = 0;

    @Column(name = "completed_interventions")
    private Integer completedInterventions = 0;

    @Column(name = "projects_managed")
    private Integer projectsManaged = 0;

    @Column(name = "success_rate")
    private Double successRate = 0.0;

    @Column(name = "experience_points")
    private Integer experiencePoints = 0;

    @Column(name = "level")
    private Integer level = 1;

    @Column(name = "badges_earned")
    private String badgesEarned; // JSON string of earned badges

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "total_components_ordered")
    private Integer totalComponentsOrdered = 0;

    @Column(name = "preventive_interventions")
    private Integer preventiveInterventions = 0;

    @Column(name = "curative_interventions")
    private Integer curativeInterventions = 0;

    @Column(name = "average_completion_time")
    private Double averageCompletionTime = 0.0; // in hours

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
        // Calculate success rate
        if (totalInterventions > 0) {
            successRate = (double) completedInterventions / totalInterventions * 100;
        }
        // Calculate level based on experience points
        level = calculateLevel(experiencePoints);
    }

    private Integer calculateLevel(Integer xp) {
        if (xp == null || xp < 0) return 1;
        // Level calculation: 100 XP for level 1, then +200 XP per level
        // Level 1: 0-99 XP, Level 2: 100-299 XP, Level 3: 300-599 XP, etc.
        return (xp / 100) + 1;
    }

    public void addExperiencePoints(Integer points) {
        if (points != null && points > 0) {
            this.experiencePoints = (this.experiencePoints == null ? 0 : this.experiencePoints) + points;
        }
    }

    public void incrementInterventions() {
        this.totalInterventions = (this.totalInterventions == null ? 0 : this.totalInterventions) + 1;
    }

    public void incrementCompletedInterventions() {
        this.completedInterventions = (this.completedInterventions == null ? 0 : this.completedInterventions) + 1;
        addExperiencePoints(50); // 50 XP per completed intervention
    }

    public void incrementProjectsManaged() {
        this.projectsManaged = (this.projectsManaged == null ? 0 : this.projectsManaged) + 1;
        addExperiencePoints(100); // 100 XP per project managed
    }

    public void incrementComponentsOrdered() {
        this.totalComponentsOrdered = (this.totalComponentsOrdered == null ? 0 : this.totalComponentsOrdered) + 1;
        addExperiencePoints(10); // 10 XP per component ordered
    }
}
