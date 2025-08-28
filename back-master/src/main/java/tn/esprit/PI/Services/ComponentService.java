package tn.esprit.PI.Services;


import lombok.AllArgsConstructor;
import tn.esprit.PI.entity.Component;
import org.springframework.stereotype.Service;
import tn.esprit.PI.repository.ComponentRp;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ComponentService implements  IComponentService {

    private final ComponentRp componentRp;

    @Override
    public Component addOrIncrement(Component component) {
        // Assurez-vous que component n'est pas null et que ses données sont correctes
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        return componentRp.save(component);
    }



    public Component updateCompo(String trartArticle, Component component) {
        Optional<Component> existingComponent = componentRp.findByTrartArticle(trartArticle);
        if (existingComponent.isPresent()) {
            Component compToUpdate = existingComponent.get();

            // Mise à jour des champs spécifiques à la base de données
            compToUpdate.setTrartDesignation(component.getTrartDesignation()); // TRART_DESIGNATION
            compToUpdate.setTrartFamille(component.getTrartFamille()); // TRART_FAMILLE
            compToUpdate.setTrartSousFamille(component.getTrartSousFamille()); // TRART_SOUS_FAMILLE
            compToUpdate.setTrartMarque(component.getTrartMarque()); // TRART_MARQUE
            compToUpdate.setTrartQuantite(component.getTrartQuantite()); // TRART_QUANTITE
            compToUpdate.setTrartSociete(component.getTrartSociete()); // TRART_SOCIETE
            compToUpdate.setTrartUnite(component.getTrartUnite()); // TRART_UNITE
            compToUpdate.setTrartTransact(component.getTrartTransact()); // TRART_TRANSACT
            compToUpdate.setTrartTva(component.getTrartTva()); // TRART_TVA

            // Sauvegarder le composant mis à jour
            return componentRp.save(compToUpdate);
        }
        return null; // Si le composant n'existe pas, retourner null
    }


    @Override
    public List<Component> retrievecomp() {
        return componentRp.findAll(); // Retourne tous les composants
    }

    @Override
    public Boolean deleteCompByTRART_ARTICLE(String TRART_ARTICLE) {
        Optional<Component> componentOpt = componentRp.findByTrartArticle(TRART_ARTICLE);
        if (componentOpt.isPresent()) {
            componentRp.delete(componentOpt.get()); // Supprime le composant trouvé
            return true;
        }
        return false; // Retourne false si le composant n'est pas trouvé
    }


    /*public Component updateCompo(String TRART_ARTICLE, Component component) {
        Optional<Component> existingComponent = componentRp.findByTrartArticle(TRART_ARTICLE);
        if (existingComponent.isPresent()) {
            Component compToUpdate = existingComponent.get();
            // Mettez à jour les champs nécessaires
            compToUpdate.setTRART_DESIGNATION(component.getTRART_DESIGNATION());
            // autres mises à jour...
            return componentRp.save(compToUpdate);
        }
        return null;
    }*/

    @Override
    public List<Component> searchComponents(String trartArticle, String atl, String bur, String trartDesignation,
                                            String trartFamille, String trartMarque, String trartQuantite,
                                            String trartSociete, String trartSousFamille, String trartTransact,
                                            String trartTva, String trartUnite, String test) {
        return componentRp.searchComponents(trartArticle, atl, bur, trartDesignation, trartFamille,
                trartMarque, trartQuantite, trartSociete, trartSousFamille, trartTransact, trartTva, trartUnite, test);
    }



    public Component findByTrartArticle(String trartArticle) {
        return componentRp.findByTrartArticle(trartArticle)
                .orElseThrow(() -> new RuntimeException("Component not found"));
    }

}

