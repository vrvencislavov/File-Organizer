package softuni.workshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.workshop.domain.entities.FileSaving;

@Repository
public interface FileRepository extends JpaRepository<FileSaving, Integer> {
}
