package softuni.workshop.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import softuni.workshop.domain.entities.Role;
import softuni.workshop.domain.entities.User;
import softuni.workshop.repository.UserRepository;
import softuni.workshop.service.FileService;
import softuni.workshop.service.UserService;

import java.util.ArrayList;
import java.util.Collection;

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
    public String deleteUserPost(@PathVariable int id, Model model) {
        User user = new User();
        user.setId(id);

        userService.delete(user);

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

        this.userService.changeR(user);

        model.addAttribute("list2", userService.get());
        return "dir/admin";
    }
    @RequestMapping(value = "/returnRole/{id}", method = RequestMethod.GET)
    public String returnRole(@PathVariable int id, Model model) {
        User user = new User();
        user.setId(id);

        this.userService.returnRole(user);

        model.addAttribute("list2", userService.get());
        return "dir/admin";
    }

}
