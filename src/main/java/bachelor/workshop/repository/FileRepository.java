package bachelor.workshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import bachelor.workshop.domain.entities.FileSaving;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileSaving, Integer> {

    @Query("select a from FileSaving a where a.user_name like %?1%")
    List<FileSaving> findByKeyword(@Param("user_name") String user_name);



}
