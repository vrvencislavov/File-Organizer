package bachelor.workshop.web.controllers;

import bachelor.workshop.domain.entities.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import bachelor.workshop.domain.entities.User;
import bachelor.workshop.repository.UserRepository;
import bachelor.workshop.service.FileService;
import bachelor.workshop.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

@Controller
public class AdminController {

    private static String UPLOADED_FOLDER = "D:\\testFolder\\";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private  final FileService fileService;
    private final UserService userService;

    @Autowired
    public AdminController(UserRepository userRepository, ModelMapper modelMapper,
                          FileService fileService, UserService userService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;

        this.userService = userService;
    }

    @GetMapping("/admin")
    //  @PreAuthorize("hasRole('ROLE_ROOT')")
    public String getUser(Model model) {
        model.addAttribute("list2", userService.get());
        return "dir/admin";
    }


    @RequestMapping(value = "/admin/{id}", method = RequestMethod.GET)
    public String deleteUserPost(@PathVariable int id, Model model, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        user.setId(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        if(user.getId().equals(loggedUser.getId()) && loggedUser.getAuthorities().size() == 1){
            this.userService.delete(user);
            new SecurityContextLogoutHandler().logout(request, response, authentication);

            return "index";
        }

        this.userService.delete(user);

        model.addAttribute("list2", userService.get());
        return "dir/admin";
    }
    @GetMapping("/admin1")
    //  @PreAuthorize("hasRole('ROLE_ROOT')")
    public String changeRole(Model model) {
        model.addAttribute("list2", userService.get());
        return "dir/admin";
    }


    @RequestMapping(value = "/admin1/{id}", method = RequestMethod.GET)
    public String changeUserRole(@PathVariable int id, Model model) {
        User user = new User();
        user.setId(id);

        if(user.getId() != 1) {
            this.userService.changeR(user);
             model.addAttribute("list2", userService.get());
        }
        model.addAttribute("list2", userService.get());
        return "dir/admin";
    }
    @RequestMapping(value = "/returnRole/{id}", method = RequestMethod.GET)
    public String returnRole(@PathVariable int id, Model model) {
        User user = new User();
        user.setId(id);

        if(user.getId() != 1) {
            this.userService.returnRole(user);
              model.addAttribute("list2", userService.get());
        }
        model.addAttribute("list2", userService.get());
        return "dir/admin";
    }

}
