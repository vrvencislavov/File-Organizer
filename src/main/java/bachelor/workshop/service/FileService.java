package softuni.workshop.service;

import org.springframework.web.multipart.MultipartFile;
import softuni.workshop.domain.entities.FileSaving;
import softuni.workshop.domain.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileService {

     void uploadFile(MultipartFile file);


     boolean delete(FileSaving fileSaving);

      FileSaving get(int id);

     List<FileSaving> get();

     boolean changeEnableType(FileSaving file);

     List<FileSaving> sortByUsername();

     List<FileSaving> findByKeyword(String keyword);



}
