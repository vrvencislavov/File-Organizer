package bachelor.workshop.service;

import org.springframework.web.multipart.MultipartFile;
import bachelor.workshop.domain.entities.FileSaving;

import java.util.List;

public interface FileService {

     void uploadFile(MultipartFile file);


     boolean delete(FileSaving fileSaving);

      FileSaving get(int id);

     List<FileSaving> get();

     List<FileSaving> getByUser();

     boolean changeEnableType(FileSaving file);

     boolean backEnableType(FileSaving file);

     List<FileSaving> sortByUsername();

     List<FileSaving> sortByFileName();

    List<FileSaving> sortByExtension();

    List<FileSaving> findByKeyword(String keyword);


}
