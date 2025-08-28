package tn.esprit.PI.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails, Long userId, String role) {
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("role", role); // Ajouter le rôle ici
    return buildToken(extraClaims, userDetails, userId, jwtExpiration);
  }

  public String generateToken(UserDetails userDetails) {
    // Fournir un rôle par défaut si nécessaire
    return generateToken(userDetails, null, "DEFAULT_ROLE");
  }

  public String generateRefreshToken(UserDetails userDetails) {
    String token = buildToken(new HashMap<>(), userDetails, null, refreshExpiration);

    // Vérifiez que le token n'est pas null ou vide
    if (token == null || token.isEmpty()) {
      throw new IllegalStateException("Generated Refresh Token is null or empty");
    }

    return token;
  }

  public String buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          Long userId,
          long expiration
  ) {
    extraClaims.put("userId", userId);
    String token = Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();

    System.out.println("Generated Token: " + token);
    return token;
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
