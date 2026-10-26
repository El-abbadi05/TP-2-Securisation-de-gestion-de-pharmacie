package com.ensa.pharma.controller;

import com.ensa.pharma.model.Role;
import com.ensa.pharma.model.User;
import com.ensa.pharma.repository.RoleRepository;
import com.ensa.pharma.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin";
    }

    @PostMapping("/users")
    public String creerUtilisateur(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String role,
                                   RedirectAttributes ra) {
        if (userRepository.existsByUsername(username)) {
            ra.addFlashAttribute("error", "Ce nom d'utilisateur existe deja.");
            return "redirect:/admin";
        }
        Role r = roleRepository.findByName(role).orElse(null);
        if (r == null) {
            ra.addFlashAttribute("error", "Role invalide.");
            return "redirect:/admin";
        }
        Set<Role> roles = new HashSet<>();
        roles.add(r);
        userRepository.save(new User(username, passwordEncoder.encode(password), roles));
        ra.addFlashAttribute("success", "Utilisateur cree.");
        return "redirect:/admin";
    }

    @PostMapping("/users/supprimer/{id}")
    public String supprimerUtilisateur(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        userRepository.findById(id).ifPresent(u -> {
            if (u.getUsername().equals(principal.getName())) {
                ra.addFlashAttribute("error", "Vous ne pouvez pas supprimer votre propre compte.");
            } else {
                userRepository.delete(u);
                ra.addFlashAttribute("success", "Utilisateur supprime.");
            }
        });
        return "redirect:/admin";
    }
}
