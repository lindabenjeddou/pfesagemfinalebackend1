package tn.esprit.PI.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PlanningHoraireDTO {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String role;
    private String description;
    private String startDate;
    private String endDate;
    private Boolean valid;
}
