package tn.esprit.PI.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FinReelDTO(LocalDateTime dateFinReel, String rapport, BigDecimal tempsPasseH) {}