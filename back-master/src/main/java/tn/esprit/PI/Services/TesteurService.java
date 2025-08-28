package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.Testeur;
import tn.esprit.PI.repository.TesteurRepository;

import java.util.List;
import java.util.Optional;

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

    // Récupérer un testeur par atelier et ligne
    public Optional<Testeur> getTesteurByAtelierAndLigne(String atelier, String ligne) {
        return testeurRepository.findByAtelierAndLigne(atelier, ligne).stream().findFirst();
    }

    // Mettre à jour un testeur
    public Testeur updateTesteur(String atelier, String ligne, Testeur testeurDetails) {
        Optional<Testeur> optionalTesteur = getTesteurByAtelierAndLigne(atelier, ligne);
        if (optionalTesteur.isPresent()) {
            Testeur testeur = optionalTesteur.get();
            testeur.setBancTest(testeurDetails.getBancTest());
            testeur.setCodeGMAO(testeurDetails.getCodeGMAO());
            return testeurRepository.save(testeur);
        }
        return null;  // Ou gérer le cas où le testeur n'existe pas
    }

    // Supprimer un testeur
    public void deleteTesteur(String atelier, String ligne) {
        Optional<Testeur> testeur = getTesteurByAtelierAndLigne(atelier, ligne);
        testeur.ifPresent(testeurRepository::delete);
    }
}
