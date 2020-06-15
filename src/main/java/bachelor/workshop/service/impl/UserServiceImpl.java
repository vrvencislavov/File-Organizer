package softuni.workshop.service.impl;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import softuni.workshop.domain.entities.FileSaving;
import softuni.workshop.domain.entities.Role;
import softuni.workshop.domain.entities.User;
import softuni.workshop.domain.models.service.UserServiceModel;
import softuni.workshop.error.Constants;
import softuni.workshop.repository.RoleRepository;
import softuni.workshop.repository.UserRepository;
import softuni.workshop.service.RoleService;
import softuni.workshop.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {


    private static String UPLOADED_FOLDER = "D:\\testFolder\\";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleService = roleService;
    }

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserServiceModel registerUser(UserServiceModel userServiceModel) {
        this.roleService.seedRolesInDb();

        if (this.userRepository.count() == 0) {
            userServiceModel.setAuthorities(this.roleService.findAllRoles());
        } else {
            userServiceModel.setAuthorities(new LinkedHashSet<>());

            userServiceModel.getAuthorities().add(this.roleService.findByAuthority("ROLE_USER"));
        }

        User user = this.modelMapper.map(userServiceModel, User.class);
        user.setPassword(this.bCryptPasswordEncoder.encode(userServiceModel.getPassword()));

        return this.modelMapper.map(this.userRepository.saveAndFlush(user), UserServiceModel.class);
    }

    @Override
    public UserServiceModel findUserByUserName(String username) {
        return this.userRepository.findByUsername(username)
                .map(u -> this.modelMapper.map(u, UserServiceModel.class))
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));
    }

    @Override
    public boolean isAuthenticated(UserServiceModel model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        if (model.getUsername().equals(name)) {
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return this.userRepository
                .findByUsername(s)
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));
    }

    @Override
    public List<User> get() {
        return this.userRepository.findAll();
    }



    @Override
    public void changeR(User user) {

        List<User> users = this.userRepository.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Role role = this.roleRepository.findByAndAuthority("ROLE_ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        if(authentication != null){
            for (User user1 : users) {
                if(user.getId().equals(user1.getId())){
                    user1.setAuthorities(roles);
                    this.userRepository.saveAndFlush(user1);
                }
            }
        }
    }

    @Override
    public void returnRole(User user) {

        List<User> users = this.userRepository.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Role role = this.roleRepository.findByAndAuthority("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        if(authentication != null){
            for (User user1 : users) {
                if(user.getId().equals(user1.getId())){
                    user1.setAuthorities(roles);
                    this.userRepository.saveAndFlush(user1);
                }
            }
        }
    }

    @Override
    public void delete(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<User> users =  this.userRepository.findAll();

        if(authentication != null){
            for (User user1 : users) {
                if(user.getId().equals(user1.getId())){
                    try {
                        FileUtils.deleteDirectory(new File(UPLOADED_FOLDER + user1.getUsername()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        this.userRepository.delete(user);
    }

}
