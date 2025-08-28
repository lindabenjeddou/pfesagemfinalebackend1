package tn.esprit.PI.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.PI.config.JwtService;
import tn.esprit.PI.entity.Token;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.entity.TokenType;
import tn.esprit.PI.entity.UserRole;
import tn.esprit.PI.repository.*;
import org.springframework.security.core.userdetails.UserDetails;
import tn.esprit.PI.service.UserServiceImp;
import tn.esprit.PI.util.NotFoundException;


import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;


  public AuthenticationResponse register( RegisterRequest request) {
    User user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(UserRole.CHEF_PROJET)
            .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken((UserDetails) user);
    var refreshToken = jwtService.generateRefreshToken((UserDetails) user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }

  public AuthenticationResponse login(AuthenticationRequest request) {
    try {
      // Authenticate user
      var authenticationToken = new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
      );

      // Handle authentication through your authentication manager if needed

      // Check if the user exists
      var user = repository.findByEmail(request.getEmail())
              .orElseThrow(() -> new UsernameNotFoundException("User not found"));

      // Check if the user is confirmed
      if (user.getConfirmation() != 1) {
        return AuthenticationResponse.builder()
                .error("User not confirmed. Please wait for confirmation.")
                .build();
      }

      if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new BadCredentialsException("Invalid credentials");
      }

      // Retrieve the user's role
      String userRole = user.getRole().name(); // Assuming `getRole` returns an enum with a `name` method

      // Generate tokens with the user's role
      var jwtToken = jwtService.generateToken((UserDetails) user, user.getId(), userRole);
      var refreshToken = jwtService.generateRefreshToken((UserDetails) user);

      // Log tokens to verify they are being generated
      System.out.println("Generated JWT Token: " + jwtToken);
      System.out.println("Generated Refresh Token: " + refreshToken);

      saveUserToken(user, jwtToken);

      return AuthenticationResponse.builder()
              .accessToken(jwtToken)
              .refreshToken(refreshToken)
              .userId(user.getId())
              .role(userRole) // Set the user role
              .build();
    } catch (BadCredentialsException ex) {
      // Handle authentication failure (invalid credentials)
      return AuthenticationResponse.builder()
              .error("Invalid credentials")
              .build();
    }
  }



  public void updatePassword(Long userId, String currentPassword, String newPassword) {
    User user = repository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    // Check if the provided current password matches the stored hashed password
    if (passwordEncoder.matches(currentPassword, user.getPassword())) {
      // Hash and update the new password
      user.setPassword(passwordEncoder.encode(newPassword));
      repository.save(user);
    } else {
      throw new RuntimeException("Current password is incorrect");
    }
  }
  private void saveUserToken(User user, String jwtToken) {
    System.out.println("Saving token for user: " + user);
    System.out.println("Token: " + jwtToken);

    var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();

    tokenRepository.save(token);

    System.out.println("Token saved successfully");
  }




  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, (UserDetails) user)) {
        var accessToken = jwtService.generateToken((UserDetails) user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }
}


