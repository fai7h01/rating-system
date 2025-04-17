package com.luka.gamesellerrating.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;

import static com.luka.gamesellerrating.enums.Role.ADMIN;
import static com.luka.gamesellerrating.enums.Role.SELLER;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${server.port:9090}")
    private String port;
    @Value("${server.host:http://localhost}")
    private String host;
    private final KeycloakProperties keycloakProperties;

    public SecurityConfig(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(httpRequests -> httpRequests
                        .requestMatchers("/api/v1/admin/**").hasAuthority(ADMIN.getValue())
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/sellers/**").permitAll()
                        .requestMatchers(GET, "/api/v1/game-objects/**").permitAll()
                        .requestMatchers(POST,"/api/v1/game-objects").hasAuthority(SELLER.getValue())
                        .requestMatchers(PUT, "/api/v1/game-objects").hasAuthority(SELLER.getValue())
                        .requestMatchers(DELETE, "/api/v1/game-objects/*").hasAuthority(SELLER.getValue())
                        .requestMatchers(
                                "/actuator/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/v3/api-docs/**",
                                "/openapi.yml",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwt -> {

                    Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

                    Map<String, Map<String, Collection<String>>> resourceAccess = jwt.getClaim("resource_access");

                    resourceAccess.forEach((resource, resourceClaims) -> {

                        if (resource.equals(keycloakProperties.getClientId())) {

                            Collection<String> roles = resourceClaims.get("roles");

                            roles.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
                        }
                    });
                    return new JwtAuthenticationToken(jwt, grantedAuthorities);
                })))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(host + ":" + port, keycloakProperties.getAuthServerUrl()));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}