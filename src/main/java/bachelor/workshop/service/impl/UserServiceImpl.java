package bachelor.workshop.service.impl;

import bachelor.workshop.domain.entities.FileSaving;
import bachelor.workshop.repository.FileRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import bachelor.workshop.domain.entities.Role;
import bachelor.workshop.domain.entities.User;
import bachelor.workshop.domain.models.service.UserServiceModel;
import bachelor.workshop.error.Constants;
import bachelor.workshop.repository.RoleRepository;
import bachelor.workshop.repository.UserRepository;
import bachelor.workshop.service.RoleService;
import bachelor.workshop.service.UserService;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {


    private static String UPLOADED_FOLDER = "D:\\testFolder\\";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleService roleService;
    private final FileRepository fileRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder, RoleService roleService, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleService = roleService;
        this.fileRepository = fileRepository;
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
        List<User> users = this.userRepository.findAll();

        List<FileSaving> files = this.fileRepository.findAll();

        Role role = this.roleRepository.findByAndAuthority("ROLE_ROOT");

        if (authentication != null) {
            for (User user1 : users) {
                if (user.getId().equals(user1.getId())){
                    Set<Role> roles = user1.getAuthorities();

                   if(roles.size() > 1){
                        break;
                    }else{
                       try {
                           FileUtils.deleteDirectory(new File(UPLOADED_FOLDER + user1.getUsername()));
                           this.userRepository.delete(user);
                           for (FileSaving file : files) {
                               if(file.getUser_name().equals(user1.getUsername())){
                                   this.fileRepository.delete(file);
                               }
                           }

                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                }
            }
        }
    }

}

