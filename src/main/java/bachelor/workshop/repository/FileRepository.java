package bachelor.workshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import bachelor.workshop.domain.entities.FileSaving;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileSaving, Integer> {

//    @Query("select a from FileSaving a where a.user_name like %?1% and a.enable = true")
//    List<FileSaving> findByExtension(@Param("extension") String extension);

//    @Query("select a from FileSaving a where a.user_name like %?1% and a.enable = true " +
//            "or a.name like %?1%")
    @Query("select a from FileSaving a where a.user_name like %?1% and a.enable = true")
    List<FileSaving> findByKeyword(@Param("user_name") String user_name);

    Optional<FileSaving> findById(Integer id);

}
