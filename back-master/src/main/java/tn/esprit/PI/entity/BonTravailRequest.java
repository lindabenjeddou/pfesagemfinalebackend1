package tn.esprit.PI.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.util.List;

public class BonTravailRequest {

        public String description;
        public LocalDate dateCreation;
        public LocalDate dateDebut;
        public LocalDate dateFin;
        public StatutBonTravail statut;

        // ✅ Permettre à la fois Long et Object pour technicien
        public Long technicien; // technicienId

        // Nouveaux champs pour les associations
        public Long interventionId; // ID de l'intervention associée
        public String testeurCodeGMAO; // Code GMAO du testeur (équipement)

        // ✅ Setter personnalisé pour gérer les deux formats
        @JsonSetter("technicien")
        public void setTechnicien(JsonNode technicienNode) {
            if (technicienNode == null || technicienNode.isNull()) {
                this.technicien = null;
            } else if (technicienNode.isNumber()) {
                // Format: "technicien": 1
                this.technicien = technicienNode.asLong();
            } else if (technicienNode.isObject() && technicienNode.has("id")) {
                // Format: "technicien": {"id": 1, "name": "John"}
                this.technicien = technicienNode.get("id").asLong();
            } else {
                // Essayer de convertir en Long
                try {
                    this.technicien = Long.parseLong(technicienNode.asText());
                } catch (NumberFormatException e) {
                    this.technicien = null;
                }
            }
        }

        public List<ComposantQuantite> composants;

        public static class ComposantQuantite {
                public String id;
                public String designation;
                public int quantite;

                // ✅ Support pour le format complexe du frontend
                public int quantiteUtilisee; // Alias pour quantite
                public ComponentInfo component; // Pour le format complexe

                // Getter intelligent pour l'ID - PRIORITÉ au format complexe
                public String getId() {
                    // ✅ PRIORITÉ 1: Format complexe avec component.trartArticle
                    if (component != null && component.trartArticle != null && !component.trartArticle.trim().isEmpty()) {
                        return component.trartArticle;
                    }
                    // ✅ PRIORITÉ 2: Format simple avec id (seulement si c'est une string valide)
                    if (id != null && !id.trim().isEmpty()) {
                        // Vérifier que ce n'est pas un ID numérique invalide
                        try {
                            Long.parseLong(id);
                            // Si c'est un nombre, c'est probablement un ID de BonTravailComponent, pas un trartArticle
                            System.out.println("⚠️ ID numérique détecté (" + id + "), recherche dans component.trartArticle");
                            return null; // Forcer à utiliser component.trartArticle
                        } catch (NumberFormatException e) {
                            // C'est une string, probablement un trartArticle valide
                            return id;
                        }
                    }
                    return null;
                }

                // Getter intelligent pour la quantité
                public int getQuantite() {
                    if (quantite > 0) {
                        return quantite;
                    }
                    return quantiteUtilisee;
                }

                // Classe pour supporter le format complexe
                public static class ComponentInfo {
                    public String trartArticle;
                    public String trartDesignation;
                    public String trartQuantite;
                    // Autres champs si nécessaire
                }
        }
}