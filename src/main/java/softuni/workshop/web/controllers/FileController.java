package softuni.workshop.web.controllers;

import org.apache.logging.log4j.message.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import softuni.workshop.domain.entities.FileSaving;
import softuni.workshop.domain.entities.User;
import softuni.workshop.repository.UserRepository;
import softuni.workshop.service.FileService;
import softuni.workshop.service.UserService;


@Controller
@RequestMapping("/home")
public class FileController extends BaseController {

    private static String UPLOADED_FOLDER = "D:\\testFolder\\";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private  final FileService fileService;
    private final UserService userService;

    @Autowired
    public FileController(UserRepository userRepository, ModelMapper modelMapper,
                          FileService fileService, UserService userService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;

        this.userService = userService;
    }

    @GetMapping("/upload")
    public ModelAndView index() {
        return super.view("dir/upload");
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {


        fileService.uploadFile(file);


        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/home";
    }

   @GetMapping("/delete")
    public String get(Model model) {
        model.addAttribute("list", fileService.get());
        return "dir/delete";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable int id, Model model) {
        FileSaving file = new FileSaving();
        file.setId(id);

        fileService.delete(file);

        model.addAttribute("list", fileService.get());
        return "dir/delete";
    }

    @GetMapping("/enable")
    public String enable(Model model) {
        model.addAttribute("list", fileService.get());
        return "dir/delete";
    }

    @RequestMapping(value = "/enable/{id}", method = RequestMethod.GET)
    public String enableFileDownload(@PathVariable int id, Model model) {
        FileSaving file = new FileSaving();
        file.setId(id);

        fileService.changeEnableType(file);

        model.addAttribute("list", fileService.get());
        return "dir/delete";
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

        model.addAttribute("list", userService.get());
        return "dir/admin";
    }


}
