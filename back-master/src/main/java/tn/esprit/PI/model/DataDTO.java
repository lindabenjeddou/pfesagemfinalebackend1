package tn.esprit.PI.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataDTO {
    private double light;
    private double temperature;
    private double humidity;
    private double weight;
    private double gaz;
    private boolean shock;
    private Date Datetime;
}
