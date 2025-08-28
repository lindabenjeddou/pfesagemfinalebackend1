package tn.esprit.PI.Services;


import tn.esprit.PI.entity.Component;

import java.util.List;

public interface IComponentService {
    public Component addOrIncrement(Component component);
   public List<Component> retrievecomp();

   public Component updateCompo(String TRART_ARTICLE, Component component);
 public Boolean deleteCompByTRART_ARTICLE(String TRART_ARTICLE);
    List<Component> searchComponents(String trartArticle, String atl, String bur, String trartDesignation,
                                     String trartFamille, String trartMarque, String trartQuantite,
                                     String trartSociete, String trartSousFamille, String trartTransact,
                                     String trartTva, String trartUnite, String test);
}


