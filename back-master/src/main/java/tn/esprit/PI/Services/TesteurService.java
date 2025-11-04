package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.Testeur;
import tn.esprit.PI.entity.TesteurDTO;
import tn.esprit.PI.repository.TesteurRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TesteurService {
    @Autowired
    private TesteurRepository testeurRepository;

    // Créer un testeur
    public Testeur createTesteur(Testeur testeur) {
        return testeurRepository.save(testeur);
    }

    // Récupérer tous les testeurs
    public List<Testeur> getAllTesteurs() {
        return testeurRepository.findAll();
    }

    // Récupérer tous les testeurs avec DTO (évite la sérialisation circulaire)
    public List<TesteurDTO> getAllTesteursDTO() {
        try {
            List<Testeur> testeurs = testeurRepository.findAll();
            System.out.println("🔍 Nombre de testeurs trouvés: " + testeurs.size());
            
            for (Testeur testeur : testeurs) {
                if (testeur != null) {
                    System.out.println("📋 Testeur: " + testeur.getCodeGMAO() + " - " + testeur.getAtelier() + " - " + testeur.getLigne());
                } else {
                    System.out.println("⚠️ Testeur null trouvé dans la liste");
                }
            }
            
            return testeurs.stream()
                .filter(testeur -> testeur != null)
                .map(testeur -> {
                    TesteurDTO dto = new TesteurDTO();
                    dto.setCodeGMAO(testeur.getCodeGMAO());
                    dto.setAtelier(testeur.getAtelier());
                    dto.setLigne(testeur.getLigne());
                    dto.setBancTest(testeur.getBancTest());
                    dto.setInterventionIds(List.of());
                    dto.setNombreInterventions(0);
                    return dto;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la récupération des testeurs: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    // Convertir Testeur en TesteurDTO
    private TesteurDTO convertToDTO(Testeur testeur) {
        TesteurDTO dto = new TesteurDTO();
        dto.setCodeGMAO(testeur.getCodeGMAO());
        dto.setAtelier(testeur.getAtelier());
        dto.setLigne(testeur.getLigne());
        dto.setBancTest(testeur.getBancTest());
        
        // Éviter le lazy loading en initialisant des listes vides
        dto.setInterventionIds(List.of());
        dto.setNombreInterventions(0);
        
        // Essayer de récupérer les interventions de manière sécurisée
        try {
            if (testeur.getInterventions() != null && !testeur.getInterventions().isEmpty()) {
                dto.setInterventionIds(testeur.getInterventions().stream()
                    .map(intervention -> intervention.getId())
                    .collect(Collectors.toList()));
                dto.setNombreInterventions(testeur.getInterventions().size());
            }
        } catch (Exception e) {
            // En cas d'erreur de lazy loading, on garde les valeurs par défaut
            System.out.println("⚠️ Lazy loading error pour testeur " + testeur.getCodeGMAO() + ": " + e.getMessage());
        }
        
        return dto;
    }

    // Récupérer un testeur par atelier et ligne
    public Optional<Testeur> getTesteurByAtelierAndLigne(String atelier, String ligne) {
        return testeurRepository.findByAtelierAndLigne(atelier, ligne).stream().findFirst();
    }

    // Mettre à jour un testeur
    public Testeur updateTesteur(String atelier, String ligne, Testeur testeurDetails) {
        Optional<Testeur> optionalTesteur = getTesteurByAtelierAndLigne(atelier, ligne);
        if (optionalTesteur.isPresent()) {
            Testeur testeur = optionalTesteur.get();

            // Si le codeGMAO change, on doit supprimer et recréer l'entité
            if (!testeur.getCodeGMAO().equals(testeurDetails.getCodeGMAO())) {
                // Supprimer l'ancien testeur
                testeurRepository.delete(testeur);
                // Créer un nouveau testeur avec le nouveau codeGMAO
                Testeur nouveauTesteur = new Testeur();
                nouveauTesteur.setCodeGMAO(testeurDetails.getCodeGMAO());
                nouveauTesteur.setAtelier(atelier);
                nouveauTesteur.setLigne(ligne);
                nouveauTesteur.setBancTest(testeurDetails.getBancTest());
                return testeurRepository.save(nouveauTesteur);
            } else {
                // Si le codeGMAO ne change pas, on peut juste modifier bancTest
                testeur.setBancTest(testeurDetails.getBancTest());
                return testeurRepository.save(testeur);
            }
        }
        return null;  // Ou gérer le cas où le testeur n'existe pas
    }

    // Supprimer un testeur
    public void deleteTesteur(String atelier, String ligne) {
        Optional<Testeur> testeur = getTesteurByAtelierAndLigne(atelier, ligne);
        testeur.ifPresent(testeurRepository::delete);
    }
}
