package tn.esprit.PI.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.Component;
import tn.esprit.PI.repository.ComponentRp;

import java.util.List;

@Service
public class StockService {

    @Autowired
    private ComponentRp componentRepository;

    /**
     * Décrémente la quantité des composants commandés
     */
    public void decrementComponentStock(List<Component> components) {
        for (Component component : components) {
            try {
                // Récupérer la quantité actuelle
                String currentQuantityStr = component.getTrartQuantite();
                
                if (currentQuantityStr != null && !currentQuantityStr.isEmpty()) {
                    try {
                        int currentQuantity = Integer.parseInt(currentQuantityStr);
                        
                        // Décrémenter de 1 (ou vous pouvez ajouter un paramètre pour la quantité à décrémenter)
                        int newQuantity = Math.max(0, currentQuantity - 1); // Éviter les quantités négatives
                        
                        // Mettre à jour la quantité
                        component.setTrartQuantite(String.valueOf(newQuantity));
                        componentRepository.save(component);
                        
                        System.out.println("Stock mis à jour pour le composant " + 
                                         component.getTrartArticle() + ": " + 
                                         currentQuantity + " -> " + newQuantity);
                        
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur lors de la conversion de la quantité pour le composant " + 
                                         component.getTrartArticle() + ": " + currentQuantityStr);
                    }
                } else {
                    System.err.println("Quantité non définie pour le composant " + component.getTrartArticle());
                }
                
            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour du stock pour le composant " + 
                                 component.getTrartArticle() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Décrémente la quantité d'un composant spécifique avec une quantité donnée
     */
    public void decrementComponentStock(Component component, int quantityToDecrement) {
        try {
            String currentQuantityStr = component.getTrartQuantite();
            
            if (currentQuantityStr != null && !currentQuantityStr.isEmpty()) {
                try {
                    int currentQuantity = Integer.parseInt(currentQuantityStr);
                    int newQuantity = Math.max(0, currentQuantity - quantityToDecrement);
                    
                    component.setTrartQuantite(String.valueOf(newQuantity));
                    componentRepository.save(component);
                    
                    System.out.println("Stock mis à jour pour le composant " + 
                                     component.getTrartArticle() + ": " + 
                                     currentQuantity + " -> " + newQuantity);
                    
                } catch (NumberFormatException e) {
                    System.err.println("Erreur lors de la conversion de la quantité pour le composant " + 
                                     component.getTrartArticle() + ": " + currentQuantityStr);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du stock: " + e.getMessage());
        }
    }

    /**
     * Vérifie si un composant a suffisamment de stock
     */
    public boolean hasEnoughStock(Component component, int requiredQuantity) {
        try {
            String quantityStr = component.getTrartQuantite();
            if (quantityStr != null && !quantityStr.isEmpty()) {
                int currentQuantity = Integer.parseInt(quantityStr);
                return currentQuantity >= requiredQuantity;
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur lors de la vérification du stock pour " + component.getTrartArticle());
        }
        return false;
    }

    /**
     * Obtient la quantité actuelle d'un composant
     */
    public int getCurrentStock(Component component) {
        try {
            String quantityStr = component.getTrartQuantite();
            if (quantityStr != null && !quantityStr.isEmpty()) {
                return Integer.parseInt(quantityStr);
            }
        } catch (NumberFormatException e) {
            System.err.println("Erreur lors de la lecture du stock pour " + component.getTrartArticle());
        }
        return 0;
    }
}
