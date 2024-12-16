package yazlab.smartstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yazlab.smartstock.entity.Log;

public interface LogRepository extends JpaRepository<Log, Long> {
}