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

    /**
     * Crée une notification pour un technicien lors de l'assignation à une intervention
     */
    @PostMapping("/assignation-technicien")
    public ResponseEntity<String> notifyTechnicianAssignment(
            @RequestParam Long technicienId,
            @RequestParam Long interventionId,
            @RequestParam String interventionDescription) {
        try {
            notificationService.notifyTechnicianForAssignment(technicienId, interventionId, interventionDescription);
            return ResponseEntity.ok("Notification envoyée au technicien");
        } catch (Exception e) {
            System.err.println("Erreur notification technicien: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }

    /**
     * Crée des notifications pour tous les chefs de secteur lors de la création d'une nouvelle intervention
     */
    @PostMapping("/nouvelle-intervention")
    public ResponseEntity<String> notifyChefsSecteurForNewIntervention(
            @RequestParam Long interventionId,
            @RequestParam String interventionDescription) {
        
        System.out.println("📬 Endpoint /nouvelle-intervention appelé");
        System.out.println("📬 InterventionId: " + interventionId);
        System.out.println("📬 Description: " + interventionDescription);
        
        try {
            notificationService.notifyChefsSecteurForNewIntervention(interventionId, interventionDescription);
            return ResponseEntity.ok("Notifications envoyées aux chefs de secteur");
        } catch (Exception e) {
            System.err.println("❌ Erreur endpoint notification: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de l'envoi des notifications: " + e.getMessage());
        }
    }

    /**
     * Crée des notifications pour tous les magasiniers lors de la création d'un bon de travail avec composants
     */
    @PostMapping("/bon-travail-created")
    public ResponseEntity<String> notifyMagasiniersForBonTravailCreation(@RequestBody java.util.Map<String, Object> payload) {
        
        System.out.println("📬 Endpoint /bon-travail-created appelé");
        System.out.println("📬 Payload reçu: " + payload);
        
        try {
            Long bonTravailId = payload.get("bonTravailId") != null ? 
                Long.valueOf(payload.get("bonTravailId").toString()) : null;
            Long interventionId = payload.get("interventionId") != null ? 
                Long.valueOf(payload.get("interventionId").toString()) : null;
            String description = payload.get("description") != null ? 
                payload.get("description").toString() : "";
            Long technicianId = payload.get("technicianId") != null ? 
                Long.valueOf(payload.get("technicianId").toString()) : null;
            Integer componentCount = payload.get("componentCount") != null ? 
                Integer.valueOf(payload.get("componentCount").toString()) : 0;
            String componentsList = payload.get("components") != null ? 
                payload.get("components").toString() : "";
            
            System.out.println("📬 BonTravailId: " + bonTravailId);
            System.out.println("📬 InterventionId: " + interventionId);
            System.out.println("📬 ComponentCount: " + componentCount);
            
            notificationService.notifyMagasiniersForBonTravailCreation(
                bonTravailId, 
                interventionId, 
                description, 
                technicianId, 
                componentCount, 
                componentsList
            );
            
            return ResponseEntity.ok("Notifications envoyées aux magasiniers");
        } catch (Exception e) {
            System.err.println("❌ Erreur endpoint notification bon de travail: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de l'envoi des notifications: " + e.getMessage());
        }
    }
}
