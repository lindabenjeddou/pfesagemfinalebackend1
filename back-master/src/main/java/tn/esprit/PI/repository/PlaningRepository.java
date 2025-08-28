package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.PI.entity.Planing;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlaningRepository extends JpaRepository<Planing, Long> {
    List<Planing> findByUserId(Long userId);  // Récupérer les plannings d'un utilisateur
    @Query("SELECT p FROM Planing p WHERE p.user.id = :userId AND p.startDate >= :startDate AND p.endDate <= :endDate")
    List<Planing> findByUserIdAndDateRange(@Param("userId") Long userId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);


}
