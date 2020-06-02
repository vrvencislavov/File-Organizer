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
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


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

        List<UserServiceModel> userRegistrationDtos = new ArrayList<>();
        for (User user : users) {
            UserServiceModel userDto = this.modelMapper.map(user, UserServiceModel.class);

            userRegistrationDtos.add(userDto);

        }


        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                Path copyLocation = Paths
                        .get(UPLOADED_FOLDER + authentication.getName() + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
                Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

                 FileSaving fileSaving = new FileSaving();
                 fileSaving.setName(file.getOriginalFilename());
                 fileSaving.setUser_name(authentication.getName());
                 fileSaving.setFilePath(copyLocation.toString());

                 this.fileRepository.saveAndFlush(fileSaving);

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
    }

        @Override
    public void delete(FileSaving fileSaving) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<FileSaving> files =  this.fileRepository.findAll();

            if(authentication != null){
                for (FileSaving file : files) {
                    if(fileSaving.getId().equals(file.getId())){
                        String path = file.getFilePath();
                        fileSaving.setFilePath(path);

                        File file1 = new File(path);
                        file1.delete();
                    }
                }
            }

        fileRepository.delete(fileSaving);

    }

    @Override
    public FileSaving get(int id) {
        return fileRepository.getOne(id);
    }

    @Override
    public List<FileSaving> get() {
       return fileRepository.findAll();
    }

}

