package tn.esprit.PI.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BonDtoOut(
        Long id,
        Long interventionId,
        Long technicienId,
        StatutBonTravail statut,
        String description,
        LocalDate dateCreation,
        LocalDate dateDebut,
        LocalDate dateFin,
        LocalDateTime dateDebutReel,
        LocalDateTime dateFinReel,
        String rapport,
        BigDecimal tempsPasseH
) {}
