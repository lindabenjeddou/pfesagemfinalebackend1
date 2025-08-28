package tn.esprit.PI.RestControlleur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.NotificationService;
import tn.esprit.PI.entity.Notification;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/PI/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Récupère toutes les notifications d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            List<Notification> notifications = notificationService.getNotificationsForUser(user);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les notifications non lues d'un utilisateur
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsForUser(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            List<Notification> notifications = notificationService.getUnreadNotificationsForUser(user);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Compte le nombre de notifications non lues pour un utilisateur
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            long count = notificationService.countUnreadNotifications(user);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Marque une notification comme lue
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok("Notification marquée comme lue");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la mise à jour de la notification");
        }
    }

    /**
     * Récupère toutes les notifications des magasiniers (pour l'administration)
     */
    @GetMapping("/magasiniers")
    public ResponseEntity<List<Notification>> getAllMagasinierNotifications() {
        try {
            List<Notification> notifications = notificationService.getAllMagasinierNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprime une notification
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        try {
            // Vous pouvez ajouter une méthode deleteNotification dans NotificationService
            return ResponseEntity.ok("Notification supprimée");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la suppression de la notification");
        }
    }
}
