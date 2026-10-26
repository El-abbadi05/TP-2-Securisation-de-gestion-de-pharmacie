package com.ensa.pharma.controller;

import com.ensa.pharma.model.Medicament;
import com.ensa.pharma.repository.MedicamentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medicaments")
public class MedicamentController {

    private final MedicamentRepository medicamentRepository;

    public MedicamentController(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
    }

    // Consultation : tous les roles authentifies
    @GetMapping
    public String liste(Model model) {
        model.addAttribute("medicaments", medicamentRepository.findAll());
        return "medicaments";
    }

    // ADMIN : ajout
    @PostMapping("/ajouter")
    public String ajouter(@RequestParam String nom,
                          @RequestParam(required = false) String description,
                          @RequestParam double prix,
                          @RequestParam int stock,
                          RedirectAttributes ra) {
        medicamentRepository.save(new Medicament(nom, description, prix, stock));
        ra.addFlashAttribute("success", "Medicament ajoute.");
        return "redirect:/medicaments";
    }

    // ADMIN : suppression
    @PostMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        medicamentRepository.deleteById(id);
        ra.addFlashAttribute("success", "Medicament supprime.");
        return "redirect:/medicaments";
    }

    // ADMIN + PHARMACIEN : modification du stock
    @PostMapping("/stock/{id}")
    public String modifierStock(@PathVariable Long id,
                                @RequestParam int stock,
                                RedirectAttributes ra) {
        if (stock < 0) {
            ra.addFlashAttribute("error", "Le stock ne peut pas etre negatif.");
            return "redirect:/medicaments";
        }
        medicamentRepository.findById(id).ifPresent(m -> {
            m.setStock(stock);
            medicamentRepository.save(m);
        });
        ra.addFlashAttribute("success", "Stock mis a jour.");
        return "redirect:/medicaments";
    }

    // ADMIN + PHARMACIEN + CAISSIER : vente
    @PostMapping("/vendre/{id}")
    public String vendre(@PathVariable Long id,
                         @RequestParam(defaultValue = "1") int quantite,
                         RedirectAttributes ra) {
        Medicament m = medicamentRepository.findById(id).orElse(null);
        if (m == null) {
            ra.addFlashAttribute("error", "Medicament introuvable.");
        } else if (quantite <= 0 || quantite > m.getStock()) {
            ra.addFlashAttribute("error", "Quantite invalide ou stock insuffisant pour " + m.getNom() + ".");
        } else {
            m.setStock(m.getStock() - quantite);
            medicamentRepository.save(m);
            ra.addFlashAttribute("success", "Vente enregistree : " + quantite + " x " + m.getNom()
                    + " (" + String.format("%.2f", quantite * m.getPrix()) + " DH).");
        }
        return "redirect:/medicaments";
    }
}
