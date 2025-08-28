package tn.esprit.PI.RestControlleur;


import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.ProjectService;
import tn.esprit.PI.entity.Project;
import tn.esprit.PI.entity.ProjetDTO;

import java.util.List;

@RestController
@RequestMapping("/PI/projects")
@CrossOrigin(origins = "*")


public class ProjectController {
    private final ProjectService projectService;



    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/all")
    @ApiOperation(value = "Récupérer tous les projets", response = Project.class, responseContainer = "List")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjets();
        return ResponseEntity.ok(projects);
    }
    // Créer un projet

    @PostMapping("/add")
    public ResponseEntity<Project> addProject(@RequestBody Project project) {
        try {
            Project savedProject = projectService.addProject(project);
            return ResponseEntity.ok(savedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PutMapping("/{projectId}/addComponent/{componentId}")
    public ResponseEntity<Project> addComponentToProject(@PathVariable Long projectId, @PathVariable String componentId) {
        Project updatedProject = projectService.addComponentToProject(projectId, componentId);
        return ResponseEntity.ok(updatedProject);
    }

}
