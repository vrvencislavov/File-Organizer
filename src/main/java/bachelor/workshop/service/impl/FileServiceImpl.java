package softuni.workshop.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import softuni.workshop.domain.entities.FileSaving;
import softuni.workshop.domain.entities.User;
import softuni.workshop.domain.models.service.UserServiceModel;
import softuni.workshop.error.FileStorageException;
import softuni.workshop.repository.FileRepository;
import softuni.workshop.repository.UserRepository;
import softuni.workshop.service.FileService;
import softuni.workshop.service.UserService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Service
public class FileServiceImpl implements FileService {

    private static String UPLOADED_FOLDER = "D:\\testFolder\\";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final FileRepository fileRepository;


    @Autowired
    public FileServiceImpl(UserRepository userRepository, ModelMapper modelMapper, UserService userService, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.fileRepository = fileRepository;

    }

    @Override
    public void uploadFile(MultipartFile file) {
        List<User> users = this.userRepository.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<UserServiceModel> userRegistrationDtos = new ArrayList<>();
        for (User user : users) {
            UserServiceModel userDto = this.modelMapper.map(user, UserServiceModel.class);

            userRegistrationDtos.add(userDto);

        }
        FileSaving fileSaving = new FileSaving();
        List<FileSaving> savedFiles = this.fileRepository.findAll();

        try {
            if (authentication != null) {
                Path copyLocation = Paths
                        .get(UPLOADED_FOLDER + authentication.getName() + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
                Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

                LocalDateTime dateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

                String formatDate = dateTime.format(formatter);

                 fileSaving.setName(file.getOriginalFilename());
                 fileSaving.setUser_name(authentication.getName());
                 fileSaving.setFilePath(copyLocation.toString());
                 fileSaving.setEnable(false);
                 fileSaving.setDate(formatDate);

                 this.fileRepository.saveAndFlush(fileSaving);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
        for (FileSaving savedFile : savedFiles) {
            if(savedFile.getName().equals(file.getOriginalFilename())){
                try {
                    if (authentication != null) {
                        Random random = new Random();
                        int rand_int1 = random.nextInt(1000);
                        Path copyLocation = Paths
                                .get(UPLOADED_FOLDER  + authentication.getName() + File.separator +rand_int1 + " - " + StringUtils.cleanPath(file.getOriginalFilename()));
                        Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

                        fileSaving.setName(file.getOriginalFilename() + rand_int1);
                        fileSaving.setUser_name(authentication.getName());
                        fileSaving.setFilePath(copyLocation.toString());
                        fileSaving.setEnable(false);

                        this.fileRepository.saveAndFlush(fileSaving);


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new FileStorageException("Could not store file " + file.getOriginalFilename()
                            + ". Please try again!");
                }
            }
        }
    }

        @Override
    public boolean delete(FileSaving fileSaving) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<FileSaving> files =  this.fileRepository.findAll();

            if(authentication != null){
                for (FileSaving file : files) {
                    if(fileSaving.getId().equals(file.getId()) &&  authentication.getName().equals(file.getUser_name())){
                        String path = file.getFilePath();
                        fileSaving.setFilePath(path);

                        File file1 = new File(path);
                        file1.delete();

                        fileRepository.delete(fileSaving);

                        return true;
                    }
                }
            }
            return false;
    }

    @Override
    public boolean changeEnableType(FileSaving fileSaving) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<FileSaving> files = this.fileRepository.findAll();

        if(authentication != null){
            for (FileSaving file : files) {
                if(fileSaving.getId().equals(file.getId()) && authentication.getName().equals(file.getUser_name())){
                    file.setEnable(true);
                    this.fileRepository.saveAndFlush(file);

                    return true;
                }
            }
        }
        return false;
     }

    @Override
    public List<FileSaving> sortByUsername() {

        List<FileSaving> files = this.fileRepository.findAll();

        List<FileSaving> sortedFiles = files.stream()
                .sorted(Comparator.comparing(FileSaving::getUser_name))
                .collect(Collectors.toList());

        return sortedFiles;
    }

    @Override
    public FileSaving get(int id) {
        return fileRepository.getOne(id);
    }

    @Override
    public List<FileSaving> get() {
        return fileRepository.findAll();
    }

    @Override
    public List<FileSaving> findByKeyword(String keyword) {
        return this.fileRepository.findByKeyword(keyword);
    }
}


