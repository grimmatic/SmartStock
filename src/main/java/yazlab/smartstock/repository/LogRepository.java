package yazlab.smartstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yazlab.smartstock.entity.Log;
import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findTop50ByOrderByCreatedAtDesc();
}