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
@Table(name = "user_activities")
public class UserActivity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_type", nullable = false)
    private String activityType; // "INTERVENTION_CREATED", "PROJECT_COMPLETED", "COMPONENT_ORDERED", etc.

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "icon")
    private String icon; // Emoji or icon class

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "related_entity_id")
    private Long relatedEntityId; // ID of related project, intervention, etc.

    @Column(name = "related_entity_type")
    private String relatedEntityType; // "PROJECT", "INTERVENTION", "COMPONENT", etc.

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Static factory methods for common activities
    public static UserActivity createInterventionActivity(User user, String description, Long interventionId) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("INTERVENTION_CREATED");
        activity.setDescription(description);
        activity.setIcon("🔧");
        activity.setRelatedEntityId(interventionId);
        activity.setRelatedEntityType("INTERVENTION");
        return activity;
    }

    public static UserActivity createProjectActivity(User user, String description, Long projectId) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("PROJECT_MANAGED");
        activity.setDescription(description);
        activity.setIcon("📋");
        activity.setRelatedEntityId(projectId);
        activity.setRelatedEntityType("PROJECT");
        return activity;
    }

    public static UserActivity createComponentActivity(User user, String description, Long componentId) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("COMPONENT_ORDERED");
        activity.setDescription(description);
        activity.setIcon("📦");
        activity.setRelatedEntityId(componentId);
        activity.setRelatedEntityType("COMPONENT");
        return activity;
    }

    public static UserActivity createNotificationActivity(User user, String description) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setActivityType("NOTIFICATION_RECEIVED");
        activity.setDescription(description);
        activity.setIcon("🔔");
        return activity;
    }
}
