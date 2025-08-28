package tn.esprit.PI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.PI.entity.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentRp extends JpaRepository<Component,String> {

    Optional<Component> findByTrartArticle(String trartArticle);
    @Query("SELECT c FROM Component c WHERE " +
            "(:trartArticle IS NULL OR c.trartArticle LIKE %:trartArticle%) AND " +
            "(:atl IS NULL OR c.atl LIKE %:atl%) AND " +
            "(:bur IS NULL OR c.bur LIKE %:bur%) AND " +
            "(:trartDesignation IS NULL OR c.trartDesignation LIKE %:trartDesignation%) AND " +
            "(:trartFamille IS NULL OR c.trartFamille LIKE %:trartFamille%) AND " +
            "(:trartMarque IS NULL OR c.trartMarque LIKE %:trartMarque%) AND " +
            "(:trartQuantite IS NULL OR c.trartQuantite LIKE %:trartQuantite%) AND " +
            "(:trartSociete IS NULL OR c.trartSociete LIKE %:trartSociete%) AND " +
            "(:trartSousFamille IS NULL OR c.trartSousFamille LIKE %:trartSousFamille%) AND " +
            "(:trartTransact IS NULL OR c.trartTransact LIKE %:trartTransact%) AND " +
            "(:trartTva IS NULL OR c.trartTva LIKE %:trartTva%) AND " +
            "(:trartUnite IS NULL OR c.trartUnite LIKE %:trartUnite%) AND " +
            "(:test IS NULL OR c.test LIKE %:test%)")
    List<Component> searchComponents(@Param("trartArticle") String trartArticle,
                                     @Param("atl") String atl,
                                     @Param("bur") String bur,
                                     @Param("trartDesignation") String trartDesignation,
                                     @Param("trartFamille") String trartFamille,
                                     @Param("trartMarque") String trartMarque,
                                     @Param("trartQuantite") String trartQuantite,
                                     @Param("trartSociete") String trartSociete,
                                     @Param("trartSousFamille") String trartSousFamille,
                                     @Param("trartTransact") String trartTransact,
                                     @Param("trartTva") String trartTva,
                                     @Param("trartUnite") String trartUnite,
                                     @Param("test") String test);

}
