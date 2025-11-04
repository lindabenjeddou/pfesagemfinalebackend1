package tn.esprit.PI.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Définir la configuration CORS
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        corsConfiguration.setAllowCredentials(true); // Assurez-vous que les cookies sont envoyés
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        http
                .csrf().disable()
                .cors(cors -> cors.configurationSource(source)) // Utiliser la configuration CORS définie
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(
                                        "/user/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/PI/demandes/**",
                                        "/PI/intervention/**",
                                        "/PI/demandes/*/bon-travail/**",
                                        "/swagger-ui.html",
                                        "/PI/user/login",
                                        "/PI/user/**",
                                        "/PI/component/**",
                                        "/PI/projects/**",
                                        "/PI/sousprojets/**",
                                        "/PI/sousProjets/add",
                                        "/all",
                                        "/PI/demandes/create",
                                        "/PI/demandes/**",
                                        "/PI/demandes/recuperer/all",
                                        "/demandes/**",
                                        "/PI/PI/demandes/**",
                                        "/PI/planing/**",
                                        "/PI/planningHoraire/**",
                                        "/PI/PI/planningHoraire/**",
                                        "/PI/planningHoraire/add",
                                        "/PI/planningHoraire/all",
                                        "/pi/bons/**",
                                        "/PI/notifications/**",
                                        "/PI/websocket/**",
                                        "/PI/bons/**",
                                        "/PI/bon-de-travail/**",
                                        "/trucks/**",
                                        "/orders/**",
                                        "/missions/**",
                                        "/api/claims/**",
                                        "/pi/bons/**",

                                        "/PI/pi/bons/**",
                                        "/PI/testeurs/**",
                                        "/user/{id}",
                                        "/PI/testeurs/**",
                                        "/error" // Autoriser l'accès à la page d'erreur
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());

        return http.build();
    }
}