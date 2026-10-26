package com.ensa.pharma.repository;

import com.ensa.pharma.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
}
