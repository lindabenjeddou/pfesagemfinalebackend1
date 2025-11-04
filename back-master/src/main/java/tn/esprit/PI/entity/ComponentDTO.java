package tn.esprit.PI.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Data
public class ComponentDTO {
    private List<String> list;
    private Map<String, Integer> map;
    
    // Méthodes utilitaires pour déterminer le type de données
    public boolean isList() {
        return list != null && !list.isEmpty();
    }
    
    public boolean isMap() {
        return map != null && !map.isEmpty();
    }
    
    public boolean isEmpty() {
        return (list == null || list.isEmpty()) && (map == null || map.isEmpty());
    }
}
