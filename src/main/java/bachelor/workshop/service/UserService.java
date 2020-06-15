package softuni.workshop.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import softuni.workshop.domain.entities.FileSaving;
import softuni.workshop.domain.entities.Role;
import softuni.workshop.domain.entities.User;
import softuni.workshop.domain.models.binding.UserRegisterBindingModel;
import softuni.workshop.domain.models.service.UserServiceModel;

import java.util.Collection;
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
