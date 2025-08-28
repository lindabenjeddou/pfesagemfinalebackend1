package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.PI.entity.UserActivity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    
    List<UserActivity> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT ua FROM UserActivity ua WHERE ua.user.id = :userId ORDER BY ua.createdAt DESC")
    List<UserActivity> findRecentActivitiesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ua FROM UserActivity ua WHERE ua.user.id = :userId AND ua.createdAt >= :since ORDER BY ua.createdAt DESC")
    List<UserActivity> findActivitiesByUserIdSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    @Query("SELECT ua FROM UserActivity ua WHERE ua.user.id = :userId AND ua.activityType = :activityType ORDER BY ua.createdAt DESC")
    List<UserActivity> findByUserIdAndActivityType(@Param("userId") Long userId, @Param("activityType") String activityType);
    
    @Query("SELECT COUNT(ua) FROM UserActivity ua WHERE ua.user.id = :userId AND ua.createdAt >= :since")
    Long countActivitiesByUserIdSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
