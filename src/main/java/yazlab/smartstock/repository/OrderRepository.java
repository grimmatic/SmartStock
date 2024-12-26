package yazlab.smartstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yazlab.smartstock.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.Query;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderStatus(Order.OrderStatus status);

//    @Query("SELECT o FROM Order o WHERE o.orderStatus = 'PENDING' ORDER BY o.priorityScore DESC")
//    List<Order> findPendingOrdersSortedByPriority();

}