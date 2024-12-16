package yazlab.smartstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yazlab.smartstock.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}