package bachelor.workshop.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import bachelor.workshop.domain.entities.User;
import bachelor.workshop.domain.models.service.UserServiceModel;
import bachelor.workshop.repository.UserRepository;
import bachelor.workshop.service.UserService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController extends BaseController {


    private static String UPLOADED_FOLDER = "D:\\testFolder\\";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public String makeDirectoryForUser() {


            List<User> users = this.userRepository.findAll();

            List<UserServiceModel> userRegistrationDtos = new ArrayList<>();
            for (User user : users) {
                UserServiceModel userDto = this.modelMapper.map(user, UserServiceModel.class);

                userRegistrationDtos.add(userDto);
            }

        for (UserServiceModel dto : userRegistrationDtos) {
            if(this.userService.isAuthenticated(dto)){
                File file = new File(UPLOADED_FOLDER + dto.getUsername());
                file.mkdir();
            }
        }

        return "dashboard";
    }


    @GetMapping("/dash")
    public ModelAndView dashboard(){
        return super.view("home");
    }

//    @GetMapping("/")
//    @PreAuthorize("isAnonymous()")
//    public ModelAndView index() {
//        return super.view("index");
//    }

//    @GetMapping("/home")
//    @PreAuthorize("isAuthenticated()")
//    public ModelAndView home(ModelAndView modelAndView) {
//
//        return super.view("home", modelAndView);
//    }
}
