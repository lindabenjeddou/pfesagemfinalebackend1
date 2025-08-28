package tn.esprit.PI.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.PI.config.EmailService;
import tn.esprit.PI.config.JwtService;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.entity.UserRole;
import tn.esprit.PI.model.UserDTO;
import tn.esprit.PI.repository.UserRepository;
import org.springframework.beans.factory.annotation.*;
import tn.esprit.PI.util.*;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class UserServiceImp implements IserviceUser {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailService emailService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("id"));
        return users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO get(final Long id) {
        if (id == null) {
            // Handle the case where id is null, e.g., throw an exception or return an appropriate response.
            return null;
        }

        User u = userRepository.findById(id).orElse(null);
        if (u != null) {
            return mapToDTO(u);
        }
        return null;
    }

    @Override
    public Long create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getId();
    }

    @Override
    public void update(final Long id, UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntityM(userDTO, user);
        userRepository.save(user);
    }



    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    @Override
    public void delete(final Long id) {
        User u = userRepository.findById(id).orElse(null);
        if (u != null){
            userRepository.deleteById(id);
        }
    }

    @Override
    public UserDTO mapToDTO(User user, UserDTO userDTO) {
        return null;
    }
    @Transactional
    public List<UserDTO> getUnconfirmedUsers() {
        System.out.println("Fetching unconfirmed users...");
        List<User> unconfirmedUsers = userRepository.findUnconfirmedUsers();
        System.out.println("Number of unconfirmed users: " + unconfirmedUsers.size());

        // Log user details
        unconfirmedUsers.forEach(user -> {
            System.out.println("User ID: " + user.getId() + ", Email: " + user.getEmail());
        });

        return unconfirmedUsers.stream().map(this::mapToDTO).collect(Collectors.toList());
    }


    public UserDTO mapToDTO(final User user) {
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstname());
            userDTO.setLastName(user.getLastname());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhoneNumber(user.getPhoneNumber());
            userDTO.setAdress(user.getAdress());
            userDTO.setConfirmation(user.getConfirmation());
            userDTO.setRole(user.getRole());

            return userDTO;
        }
        return null;
    }

    @Override
    public User mapToEntity(final UserDTO userDTO, final User user) {
        user.setId(userDTO.getId());
        user.setFirstname(userDTO.getFirstName());
        user.setLastname(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAdress(userDTO.getAdress());
        user.setRole(userDTO.getRole());


        return user;
    }


    @Override
    public User mapToEntityM(final UserDTO userDTO, final User user) {
        user.setId(userDTO.getId());
        user.setFirstname(userDTO.getFirstName());
        user.setLastname(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        // Exclude updating the password
        // user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAdress(userDTO.getAdress());
        user.setRole(userDTO.getRole());
        return user;
    }

    @Override
    public UserDTO getLoggedInUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String token = parseJwtTokenFromHeader(request);

            if (token != null) {
                String username = jwtService.extractUsername(token);
                User u = userRepository.findByEmail(username).orElse(null);
                if (u != null) {
                    return mapToDTO(u);
                }
            }
            log.error("JWT token is missing or invalid.");
        } catch (Exception e) {
            log.error("An error occurred.");
        }
        return null;
    }

    private String parseJwtTokenFromHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }



    @Override
    public List<String> retrieveMAGASINIERsByEmail() {

        List<User> MAGASINIERs = userRepository.findByRole(UserRole.MAGASINIER);
        return MAGASINIERs.stream()
                .map(User::getEmail)
                .collect(Collectors.toList());    }

    @Override
    public List<String> retrieveMAGASINIERsByName() {
        List<User> MAGASINIERs = userRepository.findByRole(UserRole.MAGASINIER);
        return MAGASINIERs.stream()
                .map(MAGASINIER -> MAGASINIER.getFirstname() + " " + MAGASINIER.getLastname()+"/" +MAGASINIER.getEmail())
                .collect(Collectors.toList());
    }
    @Override
    public long getUserCount() {
        return userRepository.count();
    }
    @Override
    public Long RetrieveveUserIdByEmail(String Email) {

        User u = userRepository.findByEmail(Email).get();
        return u.getId();

    }
    @Override
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Generate reset token and set expiration
        String resetToken = generateResetToken();
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(3);

        // Update user entity
        user.setResetToken(resetToken);
        user.setResetTokenExpiration(expirationTime);

        userRepository.save(user);
        // Send email with reset link
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    public String getUserEmail(Long userId) {
        // Fetch the user by ID from the repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        // Return the user's email
        return user.getEmail();
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    public void confirmUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setConfirmation(1);

        userRepository.save(user);
    }
    @Override
    public void completePasswordReset(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .filter(User::isResetTokenValid)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        // Update password and clear reset token fields
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiration(null);

        userRepository.save(user);
    }


    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }



}
