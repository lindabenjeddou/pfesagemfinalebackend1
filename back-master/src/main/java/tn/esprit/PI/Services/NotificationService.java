package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.NotificationRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Envoie une notification à tous les magasiniers lors de la création d'un sous-projet
     */
    public void notifyMagasiniersForSousProjetCreation(SousProjet sousProjet) {
        System.out.println("🔍 === DÉBUT notifyMagasiniersForSousProjetCreation ===");
        System.out.println("🔍 SousProjet: " + sousProjet.getSousProjetName() + " (ID: " + sousProjet.getId() + ")");
        
        List<User> magasiniers = userRepository.findByRole(UserRole.MAGASINIER);
        System.out.println("🔍 Magasiniers trouvés: " + (magasiniers != null ? magasiniers.size() : "null"));
        
        if (magasiniers == null || magasiniers.isEmpty()) {
            System.err.println("❌ AUCUN MAGASINIER TROUVÉ - Notification impossible!");
            return;
        }
        
        String title = "Nouveau sous-projet créé";
        String message = String.format(
            "Un nouveau sous-projet '%s' a été créé avec %d composants commandés. " +
            "Veuillez vérifier le stock et préparer les composants nécessaires.",
            sousProjet.getSousProjetName(),
            sousProjet.getComponents() != null ? sousProjet.getComponents().size() : 0
        );
        
        System.out.println("🔍 Message de notification: " + message);
        
        int notificationCount = 0;
        for (User magasinier : magasiniers) {
            try {
                System.out.println("🔔 Création notification pour: " + magasinier.getFirstname() + " " + magasinier.getLastname() + " (ID: " + magasinier.getId() + ")");
                
                Notification notification = new Notification();
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setType(NotificationType.SOUS_PROJET_CREATED);
                notification.setRecipient(magasinier);
                notification.setSousProjet(sousProjet);
                notification.setIsRead(false);
                
                Notification savedNotification = notificationRepository.save(notification);
                System.out.println("✅ Notification sauvegardée avec ID: " + savedNotification.getId());
                notificationCount++;
            } catch (Exception e) {
                System.err.println("❌ Erreur sauvegarde notification pour " + magasinier.getFirstname() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("✅ === FIN notifyMagasiniersForSousProjetCreation - " + notificationCount + " notifications créées ===");
    }

    /**
     * Envoie une notification pour la commande de composants spécifiques
     */
    public void notifyMagasiniersForComponentOrder(SousProjet sousProjet, List<Component> components) {
        List<User> magasiniers = userRepository.findByRole(UserRole.MAGASINIER);
        
        StringBuilder componentsList = new StringBuilder();
        for (Component component : components) {
            componentsList.append("- ").append(component.getTrartDesignation())
                         .append(" (").append(component.getTrartArticle()).append(")\n");
        }

        String title = "Commande de composants";
        String message = String.format(
            "Commande de composants pour le sous-projet '%s':\n\n%s\n" +
            "Veuillez mettre à jour le stock et préparer ces composants.",
            sousProjet.getSousProjetName(),
            componentsList.toString()
        );

        for (User magasinier : magasiniers) {
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(NotificationType.COMPONENT_ORDER);
            notification.setRecipient(magasinier);
            notification.setSousProjet(sousProjet);
            notification.setIsRead(false);
            
            notificationRepository.save(notification);
        }
    }

    /**
     * Récupère toutes les notifications d'un utilisateur
     */
    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    /**
     * Récupère les notifications non lues d'un utilisateur
     */
    public List<Notification> getUnreadNotificationsForUser(User user) {
        return notificationRepository.findByRecipientAndIsReadOrderByCreatedAtDesc(user, false);
    }

    /**
     * Marque une notification comme lue
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Compte le nombre de notifications non lues pour un utilisateur
     */
    public long countUnreadNotifications(User user) {
        return notificationRepository.countByRecipientAndIsRead(user, false);
    }

    /**
     * Récupère toutes les notifications des magasiniers
     */
    public List<Notification> getAllMagasinierNotifications() {
        return notificationRepository.findAllMagasinierNotifications();
    }

    /**
     * Crée une notification de commande de composants pour les magasiniers
     */
    public void createComponentOrderNotification(Long sousProjetId, String sousProjetName, List<String> componentIds) {
        System.out.println("🔍 DEBUG SERVICE - Début createComponentOrderNotification");
        System.out.println("  - sousProjetId: " + sousProjetId);
        System.out.println("  - sousProjetName: " + sousProjetName);
        System.out.println("  - componentIds: " + componentIds);
        
        List<User> magasiniers = userRepository.findByRole(UserRole.MAGASINIER);
        System.out.println("🔍 DEBUG SERVICE - Magasiniers trouvés: " + (magasiniers != null ? magasiniers.size() : "null"));
        
        if (magasiniers != null) {
            for (User magasinier : magasiniers) {
                System.out.println("  - Magasinier: " + magasinier.getFirstname() + " " + magasinier.getLastname() + " (ID: " + magasinier.getId() + ")");
            }
        }
        
        String title = "📦 Nouvelle Commande de Composants";
        String message = String.format(
            "Commande de %d composant(s) pour le sous-projet '%s'.\n\n" +
            "Composants commandés: %s\n\n" +
            "Veuillez vérifier le stock et préparer ces composants.",
            componentIds.size(),
            sousProjetName,
            String.join(", ", componentIds)
        );
        
        System.out.println("🔍 DEBUG SERVICE - Message de notification: " + message);

        int notificationCount = 0;
        for (User magasinier : magasiniers) {
            try {
                System.out.println("🔍 DEBUG SERVICE - Création notification pour: " + magasinier.getFirstname() + " " + magasinier.getLastname());
                
                Notification notification = new Notification();
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setType(NotificationType.COMPONENT_ORDER);
                notification.setRecipient(magasinier);
                notification.setIsRead(false);
                
                Notification savedNotification = notificationRepository.save(notification);
                System.out.println("✅ DEBUG SERVICE - Notification sauvegardée avec ID: " + savedNotification.getId());
                notificationCount++;
            } catch (Exception e) {
                System.err.println("❌ DEBUG SERVICE - Erreur lors de la sauvegarde pour " + magasinier.getFirstname() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("✅ DEBUG SERVICE - Fin createComponentOrderNotification - " + notificationCount + " notifications créées");
    }
}
