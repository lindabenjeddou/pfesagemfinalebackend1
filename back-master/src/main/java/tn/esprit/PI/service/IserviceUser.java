package tn.esprit.PI.service;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.entity.UserRole;
import tn.esprit.PI.model.UserDTO;

import java.util.List;

public interface IserviceUser {
    public List<UserDTO> findAll() ;
    public List<User> getUsersByRole(UserRole role) ;

    public UserDTO get(final Long id) ;
    public Long create(final UserDTO userDTO) ;
    public User mapToEntityM(final UserDTO userDTO, final User user) ;

    public void update(final Long id, final UserDTO userDTO) ;
    List<UserDTO> getUnconfirmedUsers();
    public void delete(final Long id) ;

    public UserDTO mapToDTO(final User user, final UserDTO userDTO) ;

    public User mapToEntity(final UserDTO userDTO, final User user) ;

    public UserDTO getLoggedInUser();
    public Long RetrieveveUserIdByEmail(String Email);

   // public String RetrieveEmailByRole(UserRole role);
    public List<String> retrieveMAGASINIERsByEmail();
    public List<String> retrieveMAGASINIERsByName();

    public void completePasswordReset(String token, String newPassword) ;

    public void initiatePasswordReset(String email) ;
    public long getUserCount() ;


    String getUserEmail(Long id);
}
