package com.ensa.pharma.service;

import com.ensa.pharma.model.Medicament;
import com.ensa.pharma.model.Role;
import com.ensa.pharma.model.User;
import com.ensa.pharma.repository.MedicamentRepository;
import com.ensa.pharma.repository.RoleRepository;
import com.ensa.pharma.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Initialise les roles, les utilisateurs de test et quelques medicaments
 * au premier demarrage de l'application.
 */
@Component
public class InitData implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MedicamentRepository medicamentRepository;
    private final PasswordEncoder passwordEncoder;

    public InitData(RoleRepository roleRepository, UserRepository userRepository,
                    MedicamentRepository medicamentRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.medicamentRepository = medicamentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (String r : new String[]{"ADMIN", "PHARMACIEN", "CAISSIER", "CLIENT"}) {
            if (roleRepository.findByName(r).isEmpty()) {
                roleRepository.save(new Role(r));
            }
        }

        createUser("admin", "admin123", "ADMIN");
        createUser("pharma", "pharma123", "PHARMACIEN");
        createUser("caisse", "caisse123", "CAISSIER");
        createUser("client", "client123", "CLIENT");

        if (medicamentRepository.count() == 0) {
            medicamentRepository.save(new Medicament("Doliprane 1000mg", "Antalgique / antipyretique", 15.50, 120));
            medicamentRepository.save(new Medicament("Amoxicilline 500mg", "Antibiotique", 32.00, 60));
            medicamentRepository.save(new Medicament("Ibuprofene 400mg", "Anti-inflammatoire", 21.90, 80));
            medicamentRepository.save(new Medicament("Spasfon", "Antispasmodique", 18.00, 45));
            medicamentRepository.save(new Medicament("Vitamine C 500mg", "Complement alimentaire", 25.00, 100));
        }
    }

    private void createUser(String username, String rawPassword, String roleName) {
        if (userRepository.existsByUsername(username)) return;
        Role role = roleRepository.findByName(roleName).orElseThrow();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        userRepository.save(new User(username, passwordEncoder.encode(rawPassword), roles));
    }
}
