package tn.esprit.PI.RestControlleur;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.IComponentService;
import tn.esprit.PI.entity.Component;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
@RequestMapping("/PI/component")
public class ComponentCont {

    private final IComponentService componentService;

    // PUT: Mise à jour d'un composant par TRART_ARTICLE
    @PutMapping("/update/{TRART_ARTICLE}")
    public ResponseEntity<Component> updateComp(@PathVariable("TRART_ARTICLE") String TRART_ARTICLE, @RequestBody Component component) {
        Component updatedComponent = componentService.updateCompo(TRART_ARTICLE, component);
        if (updatedComponent != null) {
            return ResponseEntity.ok(updatedComponent);
        } else {
            return ResponseEntity.notFound().build();  // Si aucun composant n'est trouvé pour ce TRART_ARTICLE
        }
    }


    @PostMapping("/addOrIncrement")
    public ResponseEntity<Component> addOrIncrementComponent(@RequestBody Component component) {
        try {
            Component result = componentService.addOrIncrement(component);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Log l'erreur
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }



    // GET: Récupérer tous les composants
    @GetMapping("/all")
    public ResponseEntity<List<Component>> getAllComponent() {
        List<Component> components = componentService.retrievecomp();
        return ResponseEntity.ok(components);
    }

    // DELETE: Supprimer un composant par TRART_ARTICLE
  @DeleteMapping("/delete/{TRART_ARTICLE}")
    public ResponseEntity<Void> deleteByTRART_ARTICLE(@PathVariable("TRART_ARTICLE") String TRART_ARTICLE) {
        boolean isDeleted = componentService.deleteCompByTRART_ARTICLE(TRART_ARTICLE);
        if (isDeleted) {
            return ResponseEntity.noContent().build();  // 204 No Content si la suppression est réussie
        } else {
            return ResponseEntity.notFound().build();  // 404 Not Found si le TRART_ARTICLE n'existe pas
        }
    }
   /* @GetMapping("/Component/retrieve-Charged-Component")
    public List<Component> retrieveChargedComp(){
        return componentService.retrieveChargedComp();
    }

    @GetMapping("/Component/retrieve-Waiting-Component")
    public List<Component> retrieveWaitingComp(){
        return componentService.retrieveWaitingComp();
    }*/

   /* @GetMapping("/Component/retrieve-recent-components")
    public List<Component> getRecentComponents() {
        return componentService.retrieveRecentComponents();
    }

    @PutMapping("/Component/changeComponent/{id}")
    public Component changeComp(@PathVariable("id") Integer id){
        return componentService.changeComp(id);
    }*/

    @GetMapping("/search")
    public ResponseEntity<List<Component>> searchComponents(
            @RequestParam(required = false) String trartArticle,
            @RequestParam(required = false) String atl,
            @RequestParam(required = false) String bur,
            @RequestParam(required = false) String trartDesignation,
            @RequestParam(required = false) String trartFamille,
            @RequestParam(required = false) String trartMarque,
            @RequestParam(required = false) String trartQuantite,
            @RequestParam(required = false) String trartSociete,
            @RequestParam(required = false) String trartSousFamille,
            @RequestParam(required = false) String trartTransact,
            @RequestParam(required = false) String trartTva,
            @RequestParam(required = false) String trartUnite,
            @RequestParam(required = false) String test) {

        List<Component> components = componentService.searchComponents(trartArticle, atl, bur, trartDesignation,
                trartFamille, trartMarque, trartQuantite, trartSociete, trartSousFamille, trartTransact,
                trartTva, trartUnite, test);
        return ResponseEntity.ok(components);
    }


}
