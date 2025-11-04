package tn.esprit.PI.entity;

import java.time.LocalDate;

public record CreateBonDTO(
        Long interventionId,
        Long technicienId,
        LocalDate dateDebutPlanifie,
        LocalDate dateFinPlanifie,
        String description
) {}
