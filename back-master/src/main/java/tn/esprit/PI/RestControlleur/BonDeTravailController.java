package tn.esprit.PI.RestControlleur;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.PI.Services.BonDeTravailService;
import tn.esprit.PI.entity.BonDeTravail;
import tn.esprit.PI.entity.BonTravailRequest;

import java.util.List;

@RestController
@RequestMapping("/pi/bons")
@RequiredArgsConstructor
public class BonDeTravailController {

    private final BonDeTravailService bonService;

    @GetMapping
    public List<BonDeTravail> getAll() {
        return bonService.getAllBonDeTravail();
    }

    @GetMapping("/{id}")
    public BonDeTravail getById(@PathVariable Long id) {
        return bonService.getBonDeTravailById(id);
    }

    @PostMapping
    public BonDeTravail create(@RequestBody BonTravailRequest request) {
        return bonService.createBonDeTravail(request);
    }

    @PutMapping("update/{id}")
    public BonDeTravail update(@PathVariable Long id, @RequestBody BonTravailRequest request) {
        return bonService.updateBonDeTravail(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bonService.deleteBonDeTravail(id);
    }
}

