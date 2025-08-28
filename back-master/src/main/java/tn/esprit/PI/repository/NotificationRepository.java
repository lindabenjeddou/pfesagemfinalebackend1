package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.PI.entity.Notification;
import tn.esprit.PI.entity.User;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    
    List<Notification> findByRecipientAndIsReadOrderByCreatedAtDesc(User recipient, Boolean isRead);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.role = 'MAGASINIER' ORDER BY n.createdAt DESC")
    List<Notification> findAllMagasinierNotifications();
    
    long countByRecipientAndIsRead(User recipient, Boolean isRead);
    
    // Additional methods for API usage with user ID
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.isRead = :isRead ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientIdAndIsReadOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("isRead") Boolean isRead);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :userId AND n.isRead = :isRead")
    Long countByRecipientIdAndIsRead(@Param("userId") Long userId, @Param("isRead") Boolean isRead);
}
