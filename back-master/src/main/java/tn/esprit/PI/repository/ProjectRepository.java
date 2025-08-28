package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.PI.entity.Project;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.projectManagerName = :managerName")
    Long countByProjectManagerName(@Param("managerName") String managerName);
    
    @Query("SELECT p FROM Project p WHERE p.projectManagerName = :managerName")
    List<Project> findByProjectManagerName(@Param("managerName") String managerName);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.projectManagerName = (SELECT CONCAT(u.firstname, ' ', u.lastname) FROM User u WHERE u.id = :userId)")
    Long countByProjectManagerId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Project p WHERE p.projectManagerName = (SELECT CONCAT(u.firstname, ' ', u.lastname) FROM User u WHERE u.id = :userId)")
    List<Project> findByProjectManagerId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(p.budget) FROM Project p WHERE p.projectManagerName = :managerName")
    Double sumBudgetByProjectManagerName(@Param("managerName") String managerName);
    
    @Query("SELECT AVG(p.budget) FROM Project p")
    Double getAverageBudget();
}
