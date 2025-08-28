package tn.esprit.PI.Services;


import org.springframework.beans.factory.annotation.Autowired;
import tn.esprit.PI.entity.Component;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.Project;
import tn.esprit.PI.entity.ProjetDTO;
import tn.esprit.PI.repository.ComponentRp;
import tn.esprit.PI.repository.ProjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ComponentRp componentRepository;

    @Autowired
    private ComponentService componentService;  // Inject the ComponentService

    // Nouvelle méthode pour créer un projet à partir d'un DTO
    public Project createProjetFromDTO(ProjetDTO projetDTO) {
        Project projet = new Project();
        projet.setProjectName(projetDTO.getNomProjet());
        projet.setProjectManagerName(projetDTO.getNomChefProjet());
        projet.setDescription(projetDTO.getDescription());
        projet.setDate(projetDTO.getDate());
        projet.setBudget(projetDTO.getBudget());
        // Call the instance method findByTrartArticle on componentService
        List<Component> components = projetDTO.getComponents().stream()
                .map(trartArticle -> componentService.findByTrartArticle(trartArticle))  // Use the injected service
                .collect(Collectors.toList());

        projet.setComponents(components);

        return projectRepository.save(projet);
    }

    public List<Project> getAllProjets() {
        return projectRepository.findAll();  // Find all projects from the repository
    }

    public Project addComponentToProject(Long projectId, String componentId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Component component = componentRepository.findById(componentId)
                .orElseThrow(() -> new RuntimeException("Component not found"));

        if (project.getComponents().contains(component)) {
            // Le composant est déjà associé au projet
            return project;
        }

        project.getComponents().add(component);  // Utilise l'entité Component, non l'annotation Spring
        return projectRepository.save(project);
    }
    public Project addProject(Project project) {
        // Validation des champs
        if (project.getProjectName() == null || project.getProjectName().isEmpty()) {
            throw new IllegalArgumentException("Le nom du projet est requis.");
        }
        if (project.getProjectManagerName() == null || project.getProjectManagerName().isEmpty()) {
            throw new IllegalArgumentException("Le nom du chef de projet est requis.");
        }

        // Enregistrement du projet
        return projectRepository.save(project);
    }

}
