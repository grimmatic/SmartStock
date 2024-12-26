package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yazlab.smartstock.entity.Customer;
import yazlab.smartstock.entity.Order;
import yazlab.smartstock.entity.Product;
import yazlab.smartstock.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final LogService logService;
    private final ProductService productService;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findActiveOrders() {
        return orderRepository.findByOrderStatus(Order.OrderStatus.PENDING);
    }

    public Order processOrder(Order order) {
        Customer customer = authService.getCurrentCustomer();

        // Ürünü veritabanından tam olarak çek
        Product product = productService.findById(order.getProduct().getId());
        order.setProduct(product);
        if (order.getQuantity() > 5) {
            throw new RuntimeException("Bir üründen en fazla 5 adet sipariş verebilirsiniz.");
        }
        // Stok kontrolü
        if (product.getStock() < order.getQuantity()) {
            throw new RuntimeException("Yetersiz stok: " + product.getProductName());
        }

        // Bütçe kontrolü
        double totalPrice = product.getPrice() * order.getQuantity();
        if (customer.getBudget() < totalPrice) {
            throw new RuntimeException("Yetersiz bakiye! Mevcut bakiye: " +
                    customer.getBudget() + " TL, Gerekli tutar: " + totalPrice + " TL");
        }

        order.setCustomer(customer);
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(Order.OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        logService.logOrderCreation(savedOrder);

        return savedOrder;
    }

    public synchronized Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        // Eğer sipariş zaten COMPLETED ise tekrar işlem yapma
        if (order.getOrderStatus() == Order.OrderStatus.COMPLETED) {
            throw new RuntimeException("Bu sipariş zaten tamamlanmış!");
        }

        order.setOrderStatus(status);

        if (status == Order.OrderStatus.COMPLETED) {
            // Müşteri bakiyesini güncelle
            Customer customer = order.getCustomer();
            customer.setBudget(customer.getBudget() - order.getTotalPrice());
            customer.setTotalSpent(customer.getTotalSpent() + order.getTotalPrice());

            // Ürün stoğunu güncelle
            Product product = order.getProduct();
            product.setStock(product.getStock() - order.getQuantity());
        }

        Order updatedOrder = orderRepository.save(order);
        logService.logOrderCreation(updatedOrder);

        return updatedOrder;
    }

    public List<Order> getPendingOrders() {
        return orderRepository.findByOrderStatus(Order.OrderStatus.PENDING);
    }
}