package tn.esprit.PI.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.BonDeTravail;
import tn.esprit.PI.entity.BonTravailRequest;
import tn.esprit.PI.repository.BonDeTravailRepository;
import tn.esprit.PI.repository.ComponentRp;
import tn.esprit.PI.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BonDeTravailService {

    private final BonDeTravailRepository bonDeTravailRepository;
    private final UserRepository technicienRepository;
    private final ComponentRp composantRepository;

    public List<BonDeTravail> getAllBonDeTravail() {
        return bonDeTravailRepository.findAll();
    }

    public BonDeTravail getBonDeTravailById(Long id) {
        return bonDeTravailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bon de Travail non trouvé"));
    }

    public BonDeTravail createBonDeTravail(BonTravailRequest dto) {
        BonDeTravail bon = new BonDeTravail();
        bon.setDescription(dto.description);
        bon.setDateCreation(dto.dateCreation);
        bon.setDateDebut(dto.dateDebut);
        bon.setDateFin(dto.dateFin);
        bon.setStatut(dto.statut);
        bon.setTechnicien(
                technicienRepository.findById(dto.technicien)
                        .orElseThrow(() -> new RuntimeException("Technicien non trouvé"))
        );
        bon.setComposants(
                composantRepository.findAllById(dto.composants)
        );
        return bonDeTravailRepository.save(bon);
    }

    public BonDeTravail updateBonDeTravail(Long id, BonTravailRequest dto) {
        BonDeTravail bon = getBonDeTravailById(id);
        bon.setDescription(dto.description);
        bon.setDateCreation(dto.dateCreation);
        bon.setDateDebut(dto.dateDebut);
        bon.setDateFin(dto.dateFin);
        bon.setStatut(dto.statut);
        bon.setTechnicien(
                technicienRepository.findById(dto.technicien)
                        .orElseThrow(() -> new RuntimeException("Technicien non trouvé"))
        );
        bon.setComposants(
                composantRepository.findAllById(dto.composants)
        );
        return bonDeTravailRepository.save(bon);
    }

    public void deleteBonDeTravail(Long id) {
        bonDeTravailRepository.deleteById(id);
    }
}

