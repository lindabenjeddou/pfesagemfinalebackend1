package tn.esprit.PI.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.BonDeTravailRepository;
import tn.esprit.PI.repository.ComponentRp;
import tn.esprit.PI.repository.UserRepository;
import tn.esprit.PI.repository.DemandeInterventionRepository;
import tn.esprit.PI.repository.TesteurRepository;
import tn.esprit.PI.mapper.BonTravailMapper;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BonDeTravailService {

    private final BonDeTravailRepository bonDeTravailRepository;
    private final UserRepository technicienRepository;
    private final ComponentRp composantRepository;
    private final DemandeInterventionRepository interventionRepository;
    private final TesteurRepository testeurRepository;

    public List<BonDeTravail> getAllBonDeTravail() {
        return bonDeTravailRepository.findAll();
    }

    public BonDeTravail getBonDeTravailById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du bon de travail ne peut pas être null");
        }
        return bonDeTravailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de Travail non trouvé avec l'ID: " + id));
    }


    public List<BonTravailResponse> getAll() {
        return bonDeTravailRepository.findAll().stream()
                .map(BonTravailMapper::mapToDto)
                .toList();
    }

    public BonDeTravail createBonDeTravail(BonTravailRequest dto) {
        BonDeTravail bon = new BonDeTravail();
        bon.setDescription(dto.description);
        bon.setDateCreation(dto.dateCreation);
        bon.setDateDebut(dto.dateDebut);
        bon.setDateFin(dto.dateFin);
        bon.setStatut(dto.statut);
        bon.setTechnicien(
                technicienRepository.findById(
                        java.util.Optional.ofNullable(dto.technicien)
                                .orElseThrow(() -> new IllegalArgumentException("Technicien id must not be null"))
                ).orElseThrow(() -> new RuntimeException("Technicien non trouvé"))
        );

        // Associer l'intervention si fournie
        if (dto.interventionId != null) {
            DemandeIntervention intervention = interventionRepository.findById(dto.interventionId)
                .orElseThrow(() -> new RuntimeException("Intervention non trouvée avec l'ID: " + dto.interventionId));
            bon.setIntervention(intervention);
        }

        // Associer le testeur si fourni
        if (dto.testeurCodeGMAO != null) {
            Testeur testeur = testeurRepository.findById(dto.testeurCodeGMAO)
                .orElseThrow(() -> new RuntimeException("Testeur non trouvé avec le code: " + dto.testeurCodeGMAO));
            bon.setTesteur(testeur);
        }

        // ✅ Plus besoin de findAllById ici
        List<BonTravailComponent> composantsFinal = new ArrayList<>();

        for (BonTravailRequest.ComposantQuantite cq : dto.composants) {
            Component composant = composantRepository.findById(cq.id)
                    .orElseThrow(() -> new RuntimeException("Composant non trouvé : " + cq.id));

            try {
                int stockActuel = Integer.parseInt(composant.getTrartQuantite());
                if (stockActuel >= cq.quantite) {
                    composant.setTrartQuantite(String.valueOf(stockActuel - cq.quantite));
                    composantRepository.save(composant);

                    BonTravailComponent btc = new BonTravailComponent();
                    btc.setBon(bon);
                    btc.setComponent(composant);
                    btc.setQuantiteUtilisee(cq.quantite);
                    composantsFinal.add(btc);
                } else {
                    throw new RuntimeException("Quantité insuffisante pour " + composant.getTrartArticle()
                            + " (stock: " + stockActuel + ", demandé: " + cq.quantite + ")");
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Quantité invalide dans l'article : " + composant.getTrartArticle());
            }
        }

        bon.setComposants(composantsFinal);

        return bonDeTravailRepository.save(bon);
    }

    // Créer un bon de travail basé sur une intervention existante
    public BonDeTravail createBonDeTravailFromIntervention(Long interventionId, Long technicienId, BonTravailRequest dto) {
        // Vérifier que l'intervention existe et récupérer ses informations
        if (!interventionRepository.existsById(interventionId)) {
            throw new RuntimeException("Intervention non trouvée avec l'ID: " + interventionId);
        }

        // Récupérer l'intervention avec ses associations (testeur)
        DemandeIntervention intervention = interventionRepository.findById(interventionId)
            .orElseThrow(() -> new RuntimeException("Intervention non trouvée avec l'ID: " + interventionId));

        // Vérifier que l'intervention a un testeur (équipement) associé
        if (intervention.getTesteur() == null) {
            throw new RuntimeException("L'intervention doit avoir un testeur (équipement) associé pour créer un bon de travail");
        }

        // Vérifier que le technicien existe
        User technicien = technicienRepository.findById(technicienId)
            .orElseThrow(() -> new RuntimeException("Technicien non trouvé avec l'ID: " + technicienId));

        // Créer le bon de travail
        BonDeTravail bon = new BonDeTravail();
        bon.setDescription(dto.description != null ? dto.description :
            "Bon de travail pour intervention: " + intervention.getDescription());
        bon.setDateCreation(dto.dateCreation);
        bon.setDateDebut(dto.dateDebut);
        bon.setDateFin(dto.dateFin);
        bon.setStatut(dto.statut != null ? dto.statut : StatutBonTravail.EN_ATTENTE);
        bon.setTechnicien(technicien);

        // Associer l'intervention et le testeur
        bon.setIntervention(intervention);
        bon.setTesteur(intervention.getTesteur());

        // Traiter les composants si fournis
        List<BonTravailComponent> composantsFinal = new ArrayList<>();
        if (dto.composants != null && !dto.composants.isEmpty()) {
            for (BonTravailRequest.ComposantQuantite cq : dto.composants) {
                Component composant = composantRepository.findById(cq.id)
                        .orElseThrow(() -> new RuntimeException("Composant non trouvé : " + cq.id));

                try {
                    int stockActuel = Integer.parseInt(composant.getTrartQuantite());
                    if (stockActuel >= cq.quantite) {
                        composant.setTrartQuantite(String.valueOf(stockActuel - cq.quantite));
                        composantRepository.save(composant);

                        BonTravailComponent btc = new BonTravailComponent();
                        btc.setBon(bon);
                        btc.setComponent(composant);
                        btc.setQuantiteUtilisee(cq.quantite);
                        composantsFinal.add(btc);
                    } else {
                        throw new RuntimeException("Quantité insuffisante pour " + composant.getTrartArticle()
                                + " (stock: " + stockActuel + ", demandé: " + cq.quantite + ")");
                    }
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Quantité invalide dans l'article : " + composant.getTrartArticle());
                }
            }
        }

        bon.setComposants(composantsFinal);

        return bonDeTravailRepository.save(bon);
    }

    // Récupérer tous les bons de travail d'une intervention
    public List<BonDeTravail> getBonsDeTravailByIntervention(Long interventionId) {
        return bonDeTravailRepository.findByInterventionId(interventionId);
    }

    // Récupérer tous les bons de travail d'un testeur (équipement)
    public List<BonDeTravail> getBonsDeTravailByTesteur(String testeurCodeGMAO) {
        return bonDeTravailRepository.findByTesteurCodeGMAO(testeurCodeGMAO);
    }



    @Transactional
    public BonDeTravail updateBonDeTravail(Long id, BonTravailRequest dto) {
        BonDeTravail bon = getBonDeTravailById(id);

        // Mise à jour des champs seulement s'ils ne sont pas null
        if (dto.description != null) {
            bon.setDescription(dto.description);
        }
        if (dto.dateCreation != null) {
            bon.setDateCreation(dto.dateCreation);
        }
        if (dto.dateDebut != null) {
            bon.setDateDebut(dto.dateDebut);
        }
        if (dto.dateFin != null) {
            bon.setDateFin(dto.dateFin);
        }
        if (dto.statut != null) {
            bon.setStatut(dto.statut);
        }
        if (dto.technicien != null) {
            User technicien = technicienRepository.findById(dto.technicien)
                    .orElseThrow(() -> new RuntimeException("Technicien non trouvé avec l'ID: " + dto.technicien));
            bon.setTechnicien(technicien);
        }

        // Gestion des composants - NOUVELLE APPROCHE SÉCURISÉE
        if (dto.composants != null && !dto.composants.isEmpty()) {
            System.out.println("=== MISE A JOUR DES COMPOSANTS ===");
            System.out.println("Nombre de composants à traiter: " + dto.composants.size());

            // 🔍 DEBUG: Afficher tous les composants reçus
            for (int i = 0; i < dto.composants.size(); i++) {
                var comp = dto.composants.get(i);
                System.out.println("  Composant " + i + ": ID='" + comp.getId() + "', Quantité=" + comp.getQuantite());
                System.out.println("    - Format simple: id='" + comp.id + "', quantite=" + comp.quantite);
                System.out.println("    - Format complexe: quantiteUtilisee=" + comp.quantiteUtilisee +
                                 ", component=" + (comp.component != null ? comp.component.trartArticle : "null"));
            }

            // ✅ CORRECTION: Vérifier s'il y a au moins un composant avec ID valide (quantité peut être 0)
            boolean hasValidComponents = dto.composants.stream()
                .anyMatch(cq -> cq.getId() != null && !cq.getId().trim().isEmpty());

            System.out.println("🔍 hasValidComponents: " + hasValidComponents);

            if (!hasValidComponents) {
                System.out.println("⚠️ Aucun composant avec ID valide trouvé - composants non modifiés");
                return bonDeTravailRepository.save(bon);
            }

            // ✅ SOLUTION FINALE: Modifier seulement les composants spécifiés
            List<BonTravailComponent> composantsExistants = bon.getComposants();
            if (composantsExistants == null) {
                composantsExistants = new ArrayList<>();
                bon.setComposants(composantsExistants);
                System.out.println("🔍 Aucun composant existant - création d'une nouvelle liste");
            }

            System.out.println("🔍 Composants existants avant modification: " + composantsExistants.size());

            // 🔍 DEBUG: Afficher les composants existants
            for (int i = 0; i < composantsExistants.size(); i++) {
                var comp = composantsExistants.get(i);
                System.out.println("  Existant " + i + ": ID='" + comp.getComponent().getTrartArticle() +
                                 "', Quantité=" + comp.getQuantiteUtilisee());
            }

            // Traiter chaque composant de la requête
            for (BonTravailRequest.ComposantQuantite cq : dto.composants) {
                String componentId = cq.getId();
                int componentQuantite = cq.getQuantite();

                System.out.println("Traitement composant ID: " + componentId + ", Quantité: " + componentQuantite);

                // Ignorer seulement les composants sans ID valide
                if (componentId == null || componentId.trim().isEmpty()) {
                    System.out.println("Composant ignoré: ID null ou vide");
                    continue;
                }

                // ✅ CORRECTION: Permettre quantité = 0 (pour supprimer), ignorer seulement quantité < 0
                if (componentQuantite < 0) {
                    System.out.println("Composant ignoré: quantité négative (" + componentQuantite + ")");
                    continue;
                }

                // 🔍 DEBUG: Chercher si ce composant existe déjà dans la liste
                System.out.println("🔍 Recherche du composant: '" + componentId + "' dans " + composantsExistants.size() + " composants existants");

                BonTravailComponent existingComponent = null;
                for (BonTravailComponent btc : composantsExistants) {
                    String existingId = btc.getComponent().getTrartArticle();
                    System.out.println("  - Comparaison: '" + existingId + "' == '" + componentId + "' ? " + existingId.equals(componentId));
                    if (existingId.equals(componentId)) {
                        existingComponent = btc;
                        break;
                    }
                }

                if (existingComponent != null) {
                    // Composant existe déjà - modifier ou supprimer
                    System.out.println("✅ Composant TROUVÉ: " + componentId);

                    Component composant = existingComponent.getComponent();
                    int ancienneQuantiteUtilisee = existingComponent.getQuantiteUtilisee();

                    if (componentQuantite == 0) {
                        System.out.println("🗑️ Suppression du composant existant: " + componentId);

                        // ✅ GESTION STOCK: Remettre la quantité utilisée dans le stock
                        try {
                            int stockActuel = Integer.parseInt(composant.getTrartQuantite());
                            int nouveauStock = stockActuel + ancienneQuantiteUtilisee;
                            composant.setTrartQuantite(String.valueOf(nouveauStock));
                            composantRepository.save(composant);
                            System.out.println("💰 Stock remis à jour: " + componentId + " (" + stockActuel + " + " + ancienneQuantiteUtilisee + " = " + nouveauStock + ")");
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ Impossible de mettre à jour le stock pour " + componentId + ": quantité invalide");
                        }

                        composantsExistants.remove(existingComponent);
                    } else {
                        System.out.println("🔄 Mise à jour quantité du composant: " + componentId + " (" +
                                         ancienneQuantiteUtilisee + " -> " + componentQuantite + ")");

                        // ✅ GESTION STOCK: Ajuster le stock selon la différence
                        int differenceQuantite = componentQuantite - ancienneQuantiteUtilisee;

                        if (differenceQuantite != 0) {
                            try {
                                int stockActuel = Integer.parseInt(composant.getTrartQuantite());
                                int nouveauStock = stockActuel - differenceQuantite; // Si diff positive (plus utilisé) -> stock diminue

                                if (nouveauStock < 0) {
                                    throw new RuntimeException("Stock insuffisant pour " + componentId +
                                        " (stock: " + stockActuel + ", supplément demandé: " + differenceQuantite + ")");
                                }

                                composant.setTrartQuantite(String.valueOf(nouveauStock));
                                composantRepository.save(composant);

                                if (differenceQuantite > 0) {
                                    System.out.println("📉 Stock diminué: " + componentId + " (" + stockActuel + " - " + differenceQuantite + " = " + nouveauStock + ")");
                                } else {
                                    System.out.println("📈 Stock augmenté: " + componentId + " (" + stockActuel + " + " + Math.abs(differenceQuantite) + " = " + nouveauStock + ")");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("⚠️ Impossible de mettre à jour le stock pour " + componentId + ": quantité invalide");
                            }
                        }

                        existingComponent.setQuantiteUtilisee(componentQuantite);
                        System.out.println("✅ Quantité mise à jour: " + existingComponent.getQuantiteUtilisee());
                    }
                } else {
                    // Nouveau composant - ajouter seulement si quantité > 0
                    System.out.println("❌ Composant NON TROUVÉ dans la liste existante: " + componentId);
                    if (componentQuantite == 0) {
                        System.out.println("⚠️ Composant inexistant avec quantité 0 - ignoré: " + componentId);
                        continue;
                    }

                    System.out.println("🔍 Recherche du composant dans la base de données: " + componentId);
                    Component composant = composantRepository.findById(componentId)
                            .orElseThrow(() -> new RuntimeException("Composant non trouvé avec l'ID: " + componentId));

                    // ✅ GESTION STOCK: Vérifier et décrémenter le stock pour nouveau composant
                    try {
                        int stockActuel = Integer.parseInt(composant.getTrartQuantite());
                        if (stockActuel < componentQuantite) {
                            throw new RuntimeException("Stock insuffisant pour " + componentId +
                                " (stock: " + stockActuel + ", demandé: " + componentQuantite + ")");
                        }

                        int nouveauStock = stockActuel - componentQuantite;
                        composant.setTrartQuantite(String.valueOf(nouveauStock));
                        composantRepository.save(composant);
                        System.out.println("📉 Stock décrémenté pour nouveau composant: " + componentId +
                                         " (" + stockActuel + " - " + componentQuantite + " = " + nouveauStock + ")");
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Impossible de vérifier le stock pour " + componentId + ": quantité invalide");
                    }

                    // ✅ Créer le nouveau BonTravailComponent
                    BonTravailComponent btc = new BonTravailComponent();
                    btc.setBon(bon);
                    btc.setComponent(composant);
                    btc.setQuantiteUtilisee(componentQuantite);

                    System.out.println("➕ Nouveau composant ajouté - ID: " + composant.getTrartArticle() +
                                     ", Quantité: " + btc.getQuantiteUtilisee());

                    composantsExistants.add(btc);
                    System.out.println("✅ Composant ajouté à la liste. Taille: " + composantsExistants.size());
                }
            }

            System.out.println("Composants finaux dans la liste: " + composantsExistants.size());
        }

        // ✅ Sauvegarder avec flush pour forcer la persistance
        BonDeTravail savedBon = bonDeTravailRepository.saveAndFlush(bon);

        // Log pour vérifier les quantités après sauvegarde
        System.out.println("=== BON DE TRAVAIL SAUVEGARDÉ ===");
        if (savedBon.getComposants() != null) {
            System.out.println("Nombre de composants sauvegardés: " + savedBon.getComposants().size());
            for (int i = 0; i < savedBon.getComposants().size(); i++) {
                var comp = savedBon.getComposants().get(i);
                System.out.println("  Composant " + i + ": ID=" + comp.getComponent().getTrartArticle() +
                                 ", Quantité=" + comp.getQuantiteUtilisee());
            }
        } else {
            System.out.println("ATTENTION: Aucun composant trouvé après sauvegarde!");
        }

        return savedBon;
    }

    @Transactional
    public void deleteBonDeTravail(Long id) {
        // Vérifier que le bon de travail existe
        if (!bonDeTravailRepository.existsById(id)) {
            throw new RuntimeException("Bon de Travail non trouvé avec l'ID: " + id);
        }

        try {
            // Supprimer d'abord tous les composants associés
            bonDeTravailRepository.deleteComponentsByBonId(id);

            // Maintenant supprimer le bon de travail
            bonDeTravailRepository.deleteById(id);

        } catch (Exception e) {
            throw new RuntimeException("Impossible de supprimer le bon de travail avec l'ID " + id + ". Erreur: " + e.getMessage());
        }
    }



}