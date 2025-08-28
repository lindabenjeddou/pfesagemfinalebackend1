package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.PI.entity.UserStatistics;

import java.util.Optional;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {
    
    Optional<UserStatistics> findByUserId(Long userId);
    
    @Query("SELECT us FROM UserStatistics us WHERE us.user.id = :userId")
    Optional<UserStatistics> findStatisticsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(us) FROM UserStatistics us WHERE us.level >= :level")
    Long countUsersWithLevelOrAbove(@Param("level") Integer level);
    
    @Query("SELECT AVG(us.successRate) FROM UserStatistics us")
    Double getAverageSuccessRate();
    
    @Query("SELECT MAX(us.experiencePoints) FROM UserStatistics us")
    Integer getMaxExperiencePoints();
}
