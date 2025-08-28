package tn.esprit.PI.Services;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.*;
import tn.esprit.PI.repository.ComponentRp;
import tn.esprit.PI.repository.ProjectRepository;
import tn.esprit.PI.repository.SousProjetRepository;
import tn.esprit.PI.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SousProjetService {

    @Autowired
    private SousProjetRepository sousProjetRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ComponentRp componentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ComponentService componentService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private StockService stockService;
    public SousProjet createSousProjet(SousProjetDto sousProjetDto, Long projectId) {
        SousProjet sousProjet = new SousProjet();

        // Set basic details of the SousProjet
        sousProjet.setSousProjetName(sousProjetDto.getSousProjetName());
        sousProjet.setDescription(sousProjetDto.getDescription());

        // Set totalPrice
        if (sousProjetDto.getTotalPrice() == null || sousProjetDto.getTotalPrice() <= 0) {
            throw new IllegalArgumentException("Total price must be a positive value.");
        }
        sousProjet.setTotalPrice(sousProjetDto.getTotalPrice());
        System.out.println("Total price set successfully: " + sousProjetDto.getTotalPrice());

        // Fetch and set Project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        sousProjet.setProject(project);
        System.out.println("Project set successfully: " + project.getId());

        // Fetch and set Components
        List<Component> components = sousProjetDto.getComponents().stream()
                .map(trartArticle -> {
                    Component component = componentService.findByTrartArticle(trartArticle);
                    if (component == null) {
                        throw new RuntimeException("Component not found: " + trartArticle);
                    }
                    return component;
                })
                .collect(Collectors.toList());
        sousProjet.setComponents(components);
        System.out.println("Components set successfully: " + components.size() + " components added.");

        // Fetch and set Users
        List<User> users = userRepository.findAllById(sousProjetDto.getUsers());
        if (users.isEmpty()) {
            throw new RuntimeException("No users found for provided IDs: " + sousProjetDto.getUsers());
        }
        sousProjet.setUsers(users);
        System.out.println("Users set successfully: " + users.size() + " users added.");

        // Save and return the SousProjet
        System.out.println("🔍 === DÉBUT SAUVEGARDE SOUS-PROJET ===");
        SousProjet savedSousProjet = sousProjetRepository.save(sousProjet);
        System.out.println("✅ SousProjet created successfully with ID: " + savedSousProjet.getId());
        System.out.println("🔍 Nom du sous-projet: " + savedSousProjet.getSousProjetName());
        System.out.println("🔍 Nombre de composants: " + components.size());
        
        // Envoyer une notification aux magasiniers
        System.out.println("🔍 === DÉBUT ENVOI NOTIFICATIONS ===");
        try {
            System.out.println("🔔 Appel notifyMagasiniersForSousProjetCreation...");
            notificationService.notifyMagasiniersForSousProjetCreation(savedSousProjet);
            System.out.println("✅ notifyMagasiniersForSousProjetCreation terminé");
            
            if (components != null && !components.isEmpty()) {
                System.out.println("🔔 Appel notifyMagasiniersForComponentOrder avec " + components.size() + " composants...");
                notificationService.notifyMagasiniersForComponentOrder(savedSousProjet, components);
                System.out.println("✅ notifyMagasiniersForComponentOrder terminé");
            } else {
                System.out.println("⚠️ Aucun composant à notifier");
            }
            
            System.out.println("✅ TOUTES LES NOTIFICATIONS ENVOYÉES pour le sous-projet: " + savedSousProjet.getId());
        } catch (Exception e) {
            System.err.println("❌ ERREUR lors de l'envoi des notifications: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Décrémenter le stock des composants commandés
        System.out.println("🔍 === DÉBUT MISE À JOUR STOCK ===");
        try {
            if (components != null && !components.isEmpty()) {
                stockService.decrementComponentStock(components);
                System.out.println("✅ Stock mis à jour pour " + components.size() + " composants");
            } else {
                System.out.println("⚠️ Aucun composant pour mise à jour stock");
            }
        } catch (Exception e) {
            System.err.println("❌ ERREUR lors de la mise à jour du stock: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("🔍 === FIN CRÉATION SOUS-PROJET ===");
        
        return savedSousProjet;
    }



    // Get all SousProjets
    public List<SousProjetDto> getAllSousProjets() {
        List<SousProjet> sousProjets = sousProjetRepository.findAll();
        return sousProjets.stream().map(sousProjet -> {
            SousProjetDto dto = new SousProjetDto();
            dto.setId(sousProjet.getId());
            dto.setSousProjetName(sousProjet.getSousProjetName());
            dto.setDescription(sousProjet.getDescription());

            // Set the project ID
            dto.setProjectId(sousProjet.getProject().getId());

            // Set the totalPrice
            dto.setTotalPrice(sousProjet.getTotalPrice());

            // Get the list of components
            List<String> componentIds = sousProjet.getComponents().stream()
                    .map(Component::getTrartArticle)
                    .collect(Collectors.toList());
            dto.setComponents(componentIds);

            // Get the list of users
            List<Long> userIds = sousProjet.getUsers().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            dto.setUsers(userIds);

            // Set the confirmed status
            dto.setConfirmed(sousProjet.getConfirmed()); // Ajout du champ 'confirmed'

            return dto;
        }).collect(Collectors.toList());
    }



    // Update an existing SousProjet
    public SousProjet updateSousProjet(Long id, SousProjetDto sousProjetDto) {
        Optional<SousProjet> sousProjetOptional = sousProjetRepository.findById(id);
        if (sousProjetOptional.isPresent()) {
            SousProjet existingSousProjet = sousProjetOptional.get();
            existingSousProjet.setSousProjetName(sousProjetDto.getSousProjetName());
            existingSousProjet.setDescription(sousProjetDto.getDescription());

            // Set the components (articles) for the sous-projet
            List<Component> components = componentRepository.findAllById(
                    sousProjetDto.getComponents()
            );
            existingSousProjet.setComponents(components);

            // Set the total price (new field)
            existingSousProjet.setTotalPrice(sousProjetDto.getTotalPrice());

            // Save the updated sous-projet
            return sousProjetRepository.save(existingSousProjet);
        } else {
            throw new RuntimeException("SousProjet not found with id: " + id);
        }
    }


    // Delete a SousProjet
    public void deleteSousProjet(Long id) {
        sousProjetRepository.deleteById(id);
    }

    // Get SousProjets by Project ID
    public List<SousProjet> getSousProjetsByProjectId(Long projectId) {
        return sousProjetRepository.findByProjectId(projectId);
    }


    public SousProjet getSousProjetById(Long id) {
        return sousProjetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SousProjet not found with ID: " + id));
    }



    public SousProjet confirmSousProjetAutomatically(Long id) {
        SousProjet sousProjet = sousProjetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SousProjet not found with ID: " + id));



        // Set confirmation status to 1
        sousProjet.setConfirmed(1);
        return sousProjetRepository.save(sousProjet);
    }
}
