package yazlab.smartstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yazlab.smartstock.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}