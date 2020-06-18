package bachelor.workshop.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import bachelor.workshop.domain.entities.User;
import bachelor.workshop.domain.models.binding.UserRegisterBindingModel;
import bachelor.workshop.domain.models.service.UserServiceModel;
import bachelor.workshop.service.UserService;

import java.util.List;


@Controller
@RequestMapping("/users")
public class UserController extends BaseController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView register(Model model) {
        return super.view("/user/register");
    }


    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerConfirm(@ModelAttribute UserRegisterBindingModel model, BindingResult result, Model modelMessage) {

        if(model.getUsername() == null && model.getEmail() == null
        && model.getPassword() == null && model.getConfirmPassword() == null){
            String errorMessage = "Моля въведете потребителско име";
            modelMessage.addAttribute("errorMessage", errorMessage);

            String passwordMessageConfirm = "Моля въведете парола";
            modelMessage.addAttribute("passwordMessageConfirm", passwordMessageConfirm);

            String passwordMessage = "Моля въведете парола";
            modelMessage.addAttribute("passwordMessage", passwordMessage);

            String mailMessage = "Моля въведете поща";
            modelMessage.addAttribute("mailMessage", mailMessage);
            return super.view("user/register");
        }

        if(model.getUsername() == null){
            String errorMessage = "Моля въведете потребителско име";
            modelMessage.addAttribute("errorMessage", errorMessage);
            return super.view("user/register");
        }

        List<User> users = this.userService.get();

        for (User user : users) {
            if(user.getUsername().equals(model.getUsername())){
                String userError = "Потребителското име е заето";
                modelMessage.addAttribute("userError", userError);
                return super.view("user/register");
            }
        }


        if(model.getPassword() == null){
            String passwordMessage = "Моля въведете парола";
            modelMessage.addAttribute("passwordMessage", passwordMessage);
            return super.view("user/register");
        }

        if(model.getConfirmPassword() == null){
            String passwordMessageConfirm = "Моля въведете парола";
            modelMessage.addAttribute("passwordMessageConfirm", passwordMessageConfirm);
            return super.view("user/register");
        }

        if(model.getEmail() == null){
            String mailMessage = "Моля въведете поща";
            modelMessage.addAttribute("mailMessage", mailMessage);
            return super.view("user/register");
        }

        for (User user : users) {
            if(user.getEmail().equals(model.getEmail())){
                return super.view("user/register");
            }
        }

            if (!model.getPassword().equals(model.getConfirmPassword())){
            String confirm = "Паролата не съвпада";
            modelMessage.addAttribute("confirm", confirm);

            return super.view("user/register");
        }


        this.userService.registerUser(this.modelMapper.map(model, UserServiceModel.class));

        return super.redirect("/login");
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public ModelAndView login() {
        return super.view("user/login");
    }


    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }


}
