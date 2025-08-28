package tn.esprit.PI.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tn.esprit.PI.config.EmailService;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.entity.UserRole;
import tn.esprit.PI.model.UserDTO;
import tn.esprit.PI.repository.TokenRepository;
import tn.esprit.PI.repository.UserRepository;
import tn.esprit.PI.service.UserServiceImp;
import tn.esprit.PI.util.NotFoundException;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService service;
  private final PasswordEncoder encoder;

  @Autowired
  UserRepository userRepository;

  @Autowired
  TokenRepository tokenRepository;

  @Autowired
  private UserServiceImp userService;

  @Autowired
  private EmailService emailService;

  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
  @ResponseBody
  public ResponseEntity<Object> register(
          @RequestParam String firstName,
          @RequestParam String lastName,
          @RequestParam String email,
          @RequestParam String phoneNumber,
          @RequestParam String password,
          @RequestParam String address) {
    try {
      UserRole defaultRole = UserRole.CHEF_PROJET;

      if (userService.existsByEmail(email)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
      }

      UserDTO userDTO = new UserDTO();
      userDTO.setFirstName(firstName);
      userDTO.setLastName(lastName);
      userDTO.setEmail(email);
      userDTO.setPassword(encoder.encode(password));
      userDTO.setPhoneNumber(phoneNumber);
      userDTO.setAdress(address);
      userDTO.setRole(defaultRole);
      userDTO.setConfirmation(0);

      Long id = userService.create(userDTO);

      String subject = "Welcome SAGEM MAGASIN";
      String body = "Thank you for registering! Wait for confirmation.";

      emailService.sendRegistrationEmail(email, subject, body);
      return ResponseEntity.status(HttpStatus.CREATED).body(id.toString());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed. Please check your data.");
    }
  }

  @PostMapping(value = "/register/Admin")
  @ResponseBody
  public ResponseEntity<Object> registerAdmin(
          @RequestParam String firstName,
          @RequestParam String lastName,
          @RequestParam String email,
          @RequestParam String phoneNumber,
          @RequestParam String password,
          @RequestParam String address,
          @RequestParam UserRole role) throws Exception {

    try {
      if (role == null) {
        role = UserRole.CHEF_PROJET;
      }

      if (userRepository.existsByEmail(email)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
      }
      UserDTO userDTO = new UserDTO();
      userDTO.setFirstName(firstName);
      userDTO.setLastName(lastName);
      userDTO.setEmail(email);
      userDTO.setPassword(encoder.encode(password));
      userDTO.setPhoneNumber(phoneNumber);
      userDTO.setAdress(address);
      userDTO.setRole(role);
      userDTO.setConfirmation(1);

      if (userDTO.getFirstName() == null || userDTO.getFirstName().matches(".*\\d.*")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Firstname");
      }
      if (userDTO.getLastName() == null || userDTO.getLastName().matches(".*\\d.*")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Lastname");
      }
      if (userDTO.getPhoneNumber() == null || !userDTO.getPhoneNumber().matches("\\d{8}")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Phone Number");
      }
      if (userDTO.getEmail() == null || !userDTO.getEmail().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{message: Invalid Email Address}");
      }

      Long id = userService.create(userDTO);
      String subject = "Welcome to SAGEM MAGASIN";
      String body = "Thank you for choose SAGEM MAGASIN your email is : "+email+" and your password : "+password;

      emailService.sendRegistrationEmail(email, subject, body);
      return ResponseEntity.status(HttpStatus.CREATED).body(id.toString());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed. Please check your data.");
    }
  }

  @PostMapping("/login")
  public ResponseEntity<Object> login(@RequestBody AuthenticationRequest request) {
    try {
      AuthenticationResponse response = service.login(request);

      if (response.getError() != null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.getError());
      }

      return ResponseEntity.ok(response);
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }
  }

  @PutMapping("/confirm/{id}")
  public ResponseEntity<Object> confirmUser(@PathVariable Long id) {
    try {
      userService.confirmUser(id);
      String userEmail = userService.getUserEmail(id);

      emailService.sendConfirmationEmail(userEmail);
      return ResponseEntity.ok().build();
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error confirming user.");
    }
  }

  @PutMapping("/update-password")
  public ResponseEntity<Object> updatePassword(@RequestBody UpdatePasswordRequest request) {
    try {
      service.updatePassword(request.getUserId(), request.getCurrentPassword(), request.getNewPassword());
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error updating password: " + e.getMessage());
    }
  }

  @GetMapping("/search")
  public ResponseEntity<List<User>> searchUsers(@RequestParam String searchTerm) {
    List<User> users = userRepository.findByFirstnameContainingIgnoreCase(searchTerm);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/unconfirmed")
  public ResponseEntity<List<UserDTO>> getUnconfirmedUsers() {
    List<UserDTO> unconfirmedUsers = userService.getUnconfirmedUsers();
    return ResponseEntity.ok(unconfirmedUsers);
  }

  @GetMapping("/searchByRole")
  @ResponseBody
  public ResponseEntity<List<User>> searchUsersByRole(@RequestParam UserRole role) {
    try {
      if (role == null) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      List<User> users = userService.getUsersByRole(role);
      return ResponseEntity.ok(users);
    } catch (Exception e) {
      log.error("Error searching users by role", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/refresh-token")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    service.refreshToken(request, response);
  }

  @GetMapping("/all")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<UserDTO> users = userService.findAll();
    return ResponseEntity.ok(users);
  }

  @PostMapping
  public ResponseEntity<Long> createUser(@RequestBody UserDTO userDTO) {
    Long userId = userService.create(userDTO);
    return new ResponseEntity<>(userId, HttpStatus.CREATED);
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
    try {
      userService.update(id, userDTO);
      return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    } catch (NotFoundException e) {
      return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      log.error("Error updating user with ID " + id, e);
      return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    try {
      userService.deleteUser(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (NotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      log.error("Error deleting user with ID " + id, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
