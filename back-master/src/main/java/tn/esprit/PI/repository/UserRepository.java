package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.entity.UserRole;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByFirstnameContainingIgnoreCase(String searchTerm);
    boolean existsByEmail(String email);
    Optional<User> findByResetToken(String resetToken);  // Add this line
    List<User> findByRole(UserRole role);
    User findUserByid(long id);
    @Query("SELECT u FROM User u WHERE u.confirmation = 0")
    List<User> findUnconfirmedUsers();
    long count();

}
