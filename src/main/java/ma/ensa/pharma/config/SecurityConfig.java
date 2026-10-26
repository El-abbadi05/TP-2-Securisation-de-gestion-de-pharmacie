package com.ensa.pharma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/access-denied").permitAll()

                // ADMIN : gestion des utilisateurs
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ADMIN : gestion des medicaments
                .requestMatchers("/medicaments/ajouter", "/medicaments/supprimer/**").hasRole("ADMIN")

                // ADMIN + PHARMACIEN : modification des stocks
                .requestMatchers("/medicaments/stock/**").hasAnyRole("ADMIN", "PHARMACIEN")

                // ADMIN + PHARMACIEN + CAISSIER : enregistrement des ventes
                .requestMatchers("/medicaments/vendre/**").hasAnyRole("ADMIN", "PHARMACIEN", "CAISSIER")

                // Consultation des medicaments (CLIENT = lecture seule)
                .requestMatchers("/medicaments").hasAnyRole("ADMIN", "PHARMACIEN", "CAISSIER", "CLIENT")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));

        return http.build();
    }
}
