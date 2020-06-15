package softuni.workshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import softuni.workshop.domain.entities.FileSaving;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileSaving, Integer> {

   @Query(value = "select * from FileSaving where f.user_name like %keyword%", nativeQuery = true)
    List<FileSaving> findByKeyword(@Param("keyword") String keyword);

}
