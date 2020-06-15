package bachelor.workshop.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import bachelor.workshop.domain.entities.User;
import bachelor.workshop.domain.models.service.UserServiceModel;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserServiceModel registerUser(UserServiceModel userServiceModel);

    UserServiceModel findUserByUserName(String username);

     boolean isAuthenticated(UserServiceModel model);

     List<User> get();


    void changeR(User user);

    void returnRole(User user);

    void delete(User user);


}
