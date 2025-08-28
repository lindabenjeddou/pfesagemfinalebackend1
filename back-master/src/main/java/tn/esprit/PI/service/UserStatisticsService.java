package tn.esprit.PI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserStatisticsService {

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DemandeInterventionRepository demandeInterventionRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public UserStatistics getUserStatistics(Long userId) {
        Optional<UserStatistics> stats = userStatisticsRepository.findByUserId(userId);
        if (stats.isPresent()) {
            return stats.get();
        } else {
            // Create new statistics for user
            return createInitialStatistics(userId);
        }
    }

    public UserStatistics createInitialStatistics(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        UserStatistics stats = new UserStatistics();
        stats.setUser(user);
        
        // Calculate real statistics from existing data
        updateStatisticsFromRealData(stats, userId);
        
        return userStatisticsRepository.save(stats);
    }

    public void updateStatisticsFromRealData(UserStatistics stats, Long userId) {
        // Count total interventions (demandes d'intervention)
        Long totalInterventions = demandeInterventionRepository.countByUserId(userId);
        stats.setTotalInterventions(totalInterventions.intValue());

        // Count completed interventions
        Long completedInterventions = demandeInterventionRepository.countByUserIdAndStatus(userId, StatutDemande.TERMINEE);
        stats.setCompletedInterventions(completedInterventions.intValue());

        // Count projects managed
        Long projectsManaged = projectRepository.countByProjectManagerId(userId);
        stats.setProjectsManaged(projectsManaged.intValue());

        // Calculate experience points based on activities
        Integer experiencePoints = calculateExperiencePoints(userId);
        stats.setExperiencePoints(experiencePoints);

        // Count unread notifications
        Long unreadNotifications = notificationRepository.countByRecipientIdAndIsRead(userId, false);

        // Update last updated timestamp
        stats.setLastUpdated(LocalDateTime.now());
    }

    private Integer calculateExperiencePoints(Long userId) {
        int xp = 0;
        
        // 50 XP per completed intervention
        Long completedInterventions = demandeInterventionRepository.countByUserIdAndStatus(userId, StatutDemande.TERMINEE);
        xp += completedInterventions.intValue() * 50;

        // 100 XP per project managed
        Long projectsManaged = projectRepository.countByProjectManagerId(userId);
        xp += projectsManaged.intValue() * 100;

        // 10 XP per component ordered (estimate based on sous-projets)
        Long sousProjects = demandeInterventionRepository.countByUserId(userId);
        xp += sousProjects.intValue() * 10;

        // Bonus XP for being active (base XP)
        xp += 500; // Starting bonus

        return xp;
    }

    public List<UserActivity> getUserRecentActivities(Long userId, int limit) {
        List<UserActivity> activities = userActivityRepository.findRecentActivitiesByUserId(userId);
        
        // If no activities exist, create some sample activities based on real data
        if (activities.isEmpty()) {
            createSampleActivitiesFromRealData(userId);
            activities = userActivityRepository.findRecentActivitiesByUserId(userId);
        }
        
        return activities.stream().limit(limit).toList();
    }

    private void createSampleActivitiesFromRealData(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        
        // Create activities based on real data
        List<Project> userProjects = projectRepository.findByProjectManagerId(userId);
        for (Project project : userProjects) {
            UserActivity activity = UserActivity.createProjectActivity(
                user, 
                "Projet '" + project.getProjectName() + "' géré avec succès", 
                project.getId()
            );
            userActivityRepository.save(activity);
        }

        // Create intervention activities
        List<DemandeIntervention> userInterventions = demandeInterventionRepository.findByUserId(userId);
        for (DemandeIntervention intervention : userInterventions.stream().limit(3).toList()) {
            UserActivity activity = UserActivity.createInterventionActivity(
                user,
                "Intervention '" + intervention.getDescription() + "' créée",
                intervention.getId()
            );
            userActivityRepository.save(activity);
        }
    }

    public void recordActivity(Long userId, String activityType, String description, String icon, Long relatedEntityId, String relatedEntityType) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return;

        UserActivity activity = new UserActivity();
        activity.setUser(userOpt.get());
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setIcon(icon);
        activity.setRelatedEntityId(relatedEntityId);
        activity.setRelatedEntityType(relatedEntityType);

        userActivityRepository.save(activity);
    }

    public void updateUserStatistics(Long userId) {
        UserStatistics stats = getUserStatistics(userId);
        updateStatisticsFromRealData(stats, userId);
        userStatisticsRepository.save(stats);
    }

    public Long getUnreadNotificationsCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsRead(userId, false);
    }
}
