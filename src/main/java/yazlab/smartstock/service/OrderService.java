package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yazlab.smartstock.entity.Order;
import yazlab.smartstock.repository.OrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CustomerService customerService;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findActiveOrders() {
        // TODO: Implement finding orders with PENDING or PROCESSING status
        return orderRepository.findAll();
    }

    public synchronized Order processOrder(Order order) {
        // TODO: Implement order processing logic with thread safety
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }
}
