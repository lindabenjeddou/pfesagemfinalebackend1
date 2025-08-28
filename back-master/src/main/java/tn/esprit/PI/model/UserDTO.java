package tn.esprit.PI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.PI.entity.UserRole;


@Getter
@Setter
public class UserDTO {

    private Long id;

    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;

    @Size(max = 255)
    private String email;

    @Size(max = 255)
    @JsonIgnore
    private String password;

    @Size(max = 255)
    private String phoneNumber;

    @Size(max = 255)
    private String adress;
    private String resetToken;  // Add this line


    private UserRole role;

    private Integer confirmation;



}
