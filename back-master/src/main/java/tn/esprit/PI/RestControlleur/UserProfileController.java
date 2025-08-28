package tn.esprit.PI.RestControlleur;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.service.UserStatisticsService;
import tn.esprit.PI.repository.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/PI/user-profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserStatisticsService userStatisticsService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}/statistics")
    @ApiOperation(value = "Récupérer les statistiques d'un utilisateur", response = UserStatistics.class)
    public ResponseEntity<UserStatistics> getUserStatistics(@PathVariable Long userId) {
        try {
            UserStatistics stats = userStatisticsService.getUserStatistics(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}/dashboard")
    @ApiOperation(value = "Récupérer les données du dashboard utilisateur")
    public ResponseEntity<Map<String, Object>> getUserDashboard(@PathVariable Long userId) {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Get user statistics
            UserStatistics stats = userStatisticsService.getUserStatistics(userId);
            dashboard.put("statistics", stats);
            
            // Get recent activities
            List<UserActivity> activities = userStatisticsService.getUserRecentActivities(userId, 5);
            dashboard.put("recentActivities", activities);
            
            // Get notifications count
            Long notificationsCount = userStatisticsService.getUnreadNotificationsCount(userId);
            dashboard.put("notificationsCount", notificationsCount);
            
            // Get notifications
            List<Notification> notifications = notificationRepository.findByRecipientIdAndIsReadOrderByCreatedAtDesc(userId, false);
            dashboard.put("notifications", notifications.stream().limit(3).toList());
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}/activities")
    @ApiOperation(value = "Récupérer les activités récentes d'un utilisateur")
    public ResponseEntity<List<UserActivity>> getUserActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserActivity> activities = userStatisticsService.getUserRecentActivities(userId, limit);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}/notifications")
    @ApiOperation(value = "Récupérer les notifications d'un utilisateur")
    public ResponseEntity<Map<String, Object>> getUserNotifications(@PathVariable Long userId) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Get unread notifications
            List<Notification> unreadNotifications = notificationRepository.findByRecipientIdAndIsReadOrderByCreatedAtDesc(userId, false);
            result.put("unreadNotifications", unreadNotifications);
            result.put("unreadCount", unreadNotifications.size());
            
            // Get all recent notifications (last 20)
            List<Notification> allNotifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
            result.put("allNotifications", allNotifications.stream().limit(20).toList());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{userId}/activity")
    @ApiOperation(value = "Enregistrer une nouvelle activité utilisateur")
    public ResponseEntity<String> recordUserActivity(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> activityData) {
        try {
            String activityType = (String) activityData.get("activityType");
            String description = (String) activityData.get("description");
            String icon = (String) activityData.get("icon");
            Long relatedEntityId = activityData.get("relatedEntityId") != null ? 
                Long.valueOf(activityData.get("relatedEntityId").toString()) : null;
            String relatedEntityType = (String) activityData.get("relatedEntityType");
            
            userStatisticsService.recordActivity(userId, activityType, description, icon, relatedEntityId, relatedEntityType);
            
            return ResponseEntity.ok("Activity recorded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error recording activity: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}/statistics/update")
    @ApiOperation(value = "Mettre à jour les statistiques d'un utilisateur")
    public ResponseEntity<UserStatistics> updateUserStatistics(@PathVariable Long userId) {
        try {
            userStatisticsService.updateUserStatistics(userId);
            UserStatistics updatedStats = userStatisticsService.getUserStatistics(userId);
            return ResponseEntity.ok(updatedStats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}/gamification")
    @ApiOperation(value = "Récupérer les données de gamification d'un utilisateur")
    public ResponseEntity<Map<String, Object>> getUserGamification(@PathVariable Long userId) {
        try {
            Map<String, Object> gamification = new HashMap<>();
            
            UserStatistics stats = userStatisticsService.getUserStatistics(userId);
            
            // Current level and XP
            gamification.put("level", stats.getLevel());
            gamification.put("experiencePoints", stats.getExperiencePoints());
            
            // Calculate XP for next level
            int currentLevel = stats.getLevel();
            int xpForNextLevel = currentLevel * 100 + 100; // Next level threshold
            int currentLevelMinXP = (currentLevel - 1) * 100; // Current level minimum XP
            int xpInCurrentLevel = stats.getExperiencePoints() - currentLevelMinXP;
            int xpNeededForNext = xpForNextLevel - stats.getExperiencePoints();
            
            gamification.put("xpForNextLevel", xpForNextLevel);
            gamification.put("xpInCurrentLevel", xpInCurrentLevel);
            gamification.put("xpNeededForNext", xpNeededForNext);
            gamification.put("progressPercentage", (double) xpInCurrentLevel / 100 * 100);
            
            // Badges (mock data for now, can be enhanced later)
            Map<String, Object> badges = new HashMap<>();
            badges.put("expertMaintenance", stats.getCompletedInterventions() >= 10);
            badges.put("innovator", stats.getExperiencePoints() >= 1000);
            badges.put("teamPlayer", stats.getProjectsManaged() >= 3);
            badges.put("perfectionist", stats.getSuccessRate() >= 90.0);
            
            gamification.put("badges", badges);
            
            // Level title
            String levelTitle = getLevelTitle(currentLevel);
            gamification.put("levelTitle", levelTitle);
            
            return ResponseEntity.ok(gamification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String getLevelTitle(int level) {
        if (level >= 20) return "Master Expert";
        if (level >= 15) return "Expert Senior";
        if (level >= 10) return "Expert";
        if (level >= 7) return "Spécialiste";
        if (level >= 5) return "Confirmé";
        if (level >= 3) return "Intermédiaire";
        return "Débutant";
    }

    @PostMapping("/{userId}/notifications/{notificationId}/mark-read")
    @ApiOperation(value = "Marquer une notification comme lue")
    public ResponseEntity<String> markNotificationAsRead(
            @PathVariable Long userId,
            @PathVariable Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId).orElse(null);
            if (notification != null && notification.getRecipient().getId().equals(userId)) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
                return ResponseEntity.ok("Notification marked as read");
            }
            return ResponseEntity.badRequest().body("Notification not found or access denied");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{userId}/notifications/mark-all-read")
    @ApiOperation(value = "Marquer toutes les notifications comme lues")
    public ResponseEntity<String> markAllNotificationsAsRead(@PathVariable Long userId) {
        try {
            List<Notification> unreadNotifications = notificationRepository.findByRecipientIdAndIsReadOrderByCreatedAtDesc(userId, false);
            for (Notification notification : unreadNotifications) {
                notification.setIsRead(true);
            }
            notificationRepository.saveAll(unreadNotifications);
            return ResponseEntity.ok("All notifications marked as read");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
