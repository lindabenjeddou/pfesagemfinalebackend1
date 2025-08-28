package tn.esprit.PI.auth;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import tn.esprit.PI.entity.User;

public class SecurityUtils {
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Assuming your User entity has a getId() method
            if (userDetails instanceof User) {
                return ((User) userDetails).getId();
            }
        }
        return null;
    }
}
