package bachelor.workshop.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import bachelor.workshop.domain.entities.FileSaving;
import bachelor.workshop.domain.entities.User;
import bachelor.workshop.domain.models.service.UserServiceModel;
import bachelor.workshop.error.FileStorageException;
import bachelor.workshop.repository.FileRepository;
import bachelor.workshop.repository.UserRepository;
import bachelor.workshop.service.FileService;
import bachelor.workshop.service.UserService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

                        fileSaving.setName(rand_int1 + " - " + file.getOriginalFilename());
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
    public boolean backEnableType(FileSaving fileSaving) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<FileSaving> files = this.fileRepository.findAll();

        if(authentication != null){
            for (FileSaving file : files) {
                if(fileSaving.getId().equals(file.getId()) && authentication.getName().equals(file.getUser_name())){
                    file.setEnable(false);
                    this.fileRepository.saveAndFlush(file);

                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public List<FileSaving> sortByUsername() {

        List<FileSaving> files = this.fileRepository.findAll();

        List<FileSaving> enableFiles = new ArrayList<>();

        for (FileSaving file : files) {
            if(file.isEnable() == true){
                enableFiles.add(file);
            }
        }

            List<FileSaving> sortedFiles = enableFiles.stream()
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

        List<FileSaving> files = this.fileRepository.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<FileSaving> enableFiles = new ArrayList<>();

        for (FileSaving file : files) {
            if(file.isEnable() == true){
                enableFiles.add(file);
            }
        }

        return enableFiles;
    }

    @Override
    public List<FileSaving> getByUser() {

        List<FileSaving> files = this.fileRepository.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<FileSaving> filesByName = new ArrayList<>();

        for (FileSaving file : files) {
            if(authentication.getName().equals(file.getUser_name())){
                filesByName.add(file);
            }
        }

        return filesByName;
    }

    @Override
    public List<FileSaving> findByKeyword(String keyword) {
        return this.fileRepository.findByKeyword(keyword);
    }

    @Override
    public List<FileSaving> sortByFileName() {
        List<FileSaving> listOfFiles = this.fileRepository.findAll();

        List<FileSaving> enableFiles = new ArrayList<>();

        List<String> extensionList = new ArrayList<>();

        for (FileSaving file : listOfFiles) {
            if(file.isEnable() == true){
                enableFiles.add(file);
            }
        }

        List<FileSaving> sortedFiles = enableFiles.stream()
                .sorted(Comparator.comparing(FileSaving::getName))
                .collect(Collectors.toList());

        return sortedFiles;
    }

    @Override
    public List<FileSaving> sortByExtension() {
        List<FileSaving> listOfFiles = this.fileRepository.findAll();

        List<FileSaving> enableFiles = new ArrayList<>();

        List<FileSaving> sortedFiles = new ArrayList<>();

        for (FileSaving file : listOfFiles) {
            if(file.isEnable() == true){
                enableFiles.add(file);

             String[] tokens = file.getName().split("\\.");
                sortedFiles = enableFiles.stream()
                        .sorted((t1,t2) -> tokens[1].compareTo(tokens[1]))
                        .collect(Collectors.toList());
            }
        }

        return sortedFiles;
    }
}


