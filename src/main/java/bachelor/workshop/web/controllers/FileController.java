package bachelor.workshop.web.controllers;




import bachelor.workshop.repository.FileRepository;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.aspectj.util.FileUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import bachelor.workshop.domain.entities.FileSaving;
import bachelor.workshop.domain.entities.User;
import bachelor.workshop.repository.UserRepository;
import bachelor.workshop.service.FileService;
import bachelor.workshop.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/home")
public class FileController extends BaseController {

    private static String UPLOADED_FOLDER = "D:\\testFolder\\";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private  final FileService fileService;
    private final UserService userService;

    private final FileRepository fileRepository;

    @Autowired
    public FileController(UserRepository userRepository, ModelMapper modelMapper,
                          FileService fileService, UserService userService, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
        this.userService = userService;
        this.fileRepository = fileRepository;
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

    @GetMapping("/search")
    public String search(Model model, String keyword) {

        if(keyword != null){
            model.addAttribute("list", fileService.findByKeyword(keyword));

        }else {
            model.addAttribute("list", fileService.get());
        }
        return "dir/delete";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable int id, Model model) {
        FileSaving file = new FileSaving();
        file.setId(id);

        this.fileService.delete(file);

        model.addAttribute("list", fileService.getByUser());
        return "dir/myfiles";
    }


    @GetMapping("/enable")
    public String enable(Model model) {
        model.addAttribute("list", fileService.get());
        return "dir/delete";
    }

    @GetMapping("/myFiles")
    public String myFiles(Model model) {
        model.addAttribute("list", fileService.getByUser());
        return "dir/myfiles";
    }

    @RequestMapping(value = "/enable/{id}", method = RequestMethod.GET)
    public String enableFileDownload(@PathVariable int id, Model model) {

        FileSaving fileSaving = new FileSaving();

        fileSaving.setId(id);

        if(!this.fileService.changeEnableType(fileSaving)){
            String enableMessage = "Необходими са права";
            model.addAttribute("enableMessage", enableMessage);
        }

        model.addAttribute("list", fileService.getByUser());
        return "dir/myfiles";
    }

    @RequestMapping(value = "/disable/{id}", method = RequestMethod.GET)
    public String disable(@PathVariable int id, Model model) {

        FileSaving fileSaving = new FileSaving();

        fileSaving.setId(id);

        this.fileService.backEnableType(fileSaving);

        model.addAttribute("list", fileService.getByUser());
        return "dir/myfiles";
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

    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public void download(@PathVariable int id,Model model,HttpServletRequest request, HttpServletResponse response) throws IOException {

        List<FileSaving> savingFiles = this.fileService.get();


        for (FileSaving savingFile : savingFiles) {
            if(savingFile.getId().equals(id)) {
                String path = savingFile.getFilePath();

                File file = new File(path);
                String filename = URLEncoder.encode(file.getName(),"UTF-8");

                boolean enable = savingFile.isEnable();

                if (enable == true) {

                    InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                    String mimeType = URLConnection.guessContentTypeFromStream(inputStream);

                    if (mimeType == null) {
                        mimeType = "application/octec-stream";
                    }

                    response.setContentType(mimeType + ";charset=UTF-8");
                    response.setContentLength((int) file.length());
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader("Content-Disposition", String.format("attachment; filename =\"%s\"", filename));

                    FileCopyUtils.copy(inputStream,response.getOutputStream());


                }
            }
        }

        model.addAttribute("list", fileService.get());

    }
    @RequestMapping(value = "/downloadSort/{id}", method = RequestMethod.GET)
    public void downloadSort(@PathVariable int id,Model model,HttpServletRequest request, HttpServletResponse response) throws IOException {

        List<FileSaving> savingFiles = this.fileService.get();

        for (FileSaving savingFile : savingFiles) {
            if(savingFile.getId().equals(id)) {
                String path = savingFile.getFilePath();

                File file = new File(path);
                String filename = URLEncoder.encode(savingFile.getName(),"UTF-8");

                boolean enable = savingFile.isEnable();

                if (enable == true) {

                    InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                    String mimeType = URLConnection.guessContentTypeFromStream(inputStream);

                    if (mimeType == null) {
                        mimeType = "application/octec-stream";
                    }

                    response.setContentType(mimeType + ";charset=UTF-8");
                    response.setContentLength((int) file.length());
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader("Content-Disposition", String.format("attachment; filename =\"%s\"", filename));

                    FileCopyUtils.copy(inputStream, response.getOutputStream());

                }
            }
        }
        model.addAttribute("sorting", fileService.sortByUsername());
      //  return "dir/sorted";
    }

    @GetMapping("/sort")
    public String sort(Model model, String keyword) {

        if(keyword != null){
            model.addAttribute("sorting", this.fileService.findByKeyword(keyword));
        }else {
            model.addAttribute("sorting", this.fileService.sortByUsername());
        }
        return "dir/sorted";
    }

    @GetMapping("/sortByFileName")
    public String sortByFileName(Model model, String keyword){

        if(keyword != null){
            model.addAttribute("sorting", this.fileService.findByKeyword(keyword));
        }else {
            model.addAttribute("sorting", this.fileService.sortByFileName());
        }
        return "dir/sorted";
    }

    @GetMapping("/sortByExtension")
    public String sortByExtension(Model model, String keyword){

        if(keyword != null){
            model.addAttribute("sorting", this.fileService.findByKeyword(keyword));
        }else {
            model.addAttribute("sorting", this.fileService.sortByExtension());
        }
        return "dir/sorted";
    }
}
