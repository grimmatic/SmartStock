package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yazlab.smartstock.entity.Customer;
import yazlab.smartstock.entity.Order;
import yazlab.smartstock.entity.Product;
import yazlab.smartstock.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        Product product = productService.findById(order.getProduct().getId());
        order.setProduct(product);
        order.setCustomer(customer); // Null pointer hatasını önlemek için

        if (order.getQuantity() > 5) {
            throw new RuntimeException("Bir üründen en fazla 5 adet sipariş verebilirsiniz.");
        }

        // Mevcut bekleyen (onaylanmamış) siparişleri hesaba katmadan stok kontrolü
        if (product.getStock() < order.getQuantity()) {
            throw new RuntimeException("Yetersiz stok: " + product.getProductName());
        }

        double totalPrice = product.getPrice() * order.getQuantity();
        if (customer.getBudget() < totalPrice) {
            throw new RuntimeException("Yetersiz bakiye! Mevcut bakiye: " +
                    customer.getBudget() + " TL, Gerekli tutar: " + totalPrice + " TL");
        }

        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(Order.OrderStatus.PENDING);
        order.setPriorityScore(calculatePriorityScore(order));

        Order savedOrder = orderRepository.save(order);
        logService.logOrderCreation(savedOrder);
        return savedOrder;
    }


    @Transactional
    public synchronized Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        if (order.getOrderStatus() == Order.OrderStatus.COMPLETED) {
            throw new RuntimeException("Bu sipariş zaten tamamlanmış!");
        }

        Product product = order.getProduct();

        if (status == Order.OrderStatus.COMPLETED) {
            // Yüksek öncelikli bekleyen siparişleri kontrol et
            List<Order> higherPriorityOrders = orderRepository.findByOrderStatus(Order.OrderStatus.PENDING)
                    .stream()
                    .filter(o -> o.getProduct().getId().equals(product.getId()))
                    .filter(o -> o.getPriorityScore() > order.getPriorityScore())
                    .collect(Collectors.toList());

            int reservedStock = higherPriorityOrders.stream()
                    .mapToInt(Order::getQuantity)
                    .sum();

            if (product.getStock() - reservedStock < order.getQuantity()) {
                throw new RuntimeException("Daha yüksek öncelikli siparişler için stok rezerve edilmiş");
            }

            Customer customer = order.getCustomer();
            customer.setBudget(customer.getBudget() - order.getTotalPrice());
            customer.setTotalSpent(customer.getTotalSpent() + order.getTotalPrice());
            product.setStock(product.getStock() - order.getQuantity());
            productService.save(product);
        }

        order.setOrderStatus(status);
        Order updatedOrder = orderRepository.save(order);
        if (status == Order.OrderStatus.COMPLETED) {
            logService.logOrderCreation(updatedOrder);
        }
        return updatedOrder;
    }

    public List<Order> getPendingOrders() {
        return orderRepository.findByOrderStatus(Order.OrderStatus.PENDING);
    }

    public double calculatePriorityScore(Order order) {
        // Temel Öncelik Skoru: Premium müşteriler için 15, Standart müşteriler için 10
        double basePriority = order.getCustomer().getCustomerType() == Customer.CustomerType.PREMIUM ? 15 : 10;

        // Bekleme Süresi: Sipariş oluşturulma tarihi ile şu an arasındaki süre (saniye cinsinden)
        long waitingTimeInSeconds = java.time.Duration.between(order.getOrderDate(), LocalDateTime.now()).getSeconds();

        // Bekleme Süresi Ağırlığı: Her bir saniye bekleme 0.5 puan etkiler
        double waitingWeight = 0.5;

        // Bekleme Süresi etkisi
        double waitingEffect = waitingTimeInSeconds * waitingWeight;

        // Toplam Öncelik Skoru Hesaplama
        return basePriority + waitingEffect;
    }


    public List<Order> getPendingOrdersSortedByPriority() {
        List<Order> orders = orderRepository.findByOrderStatus(Order.OrderStatus.PENDING);

        // Siparişleri dinamik olarak priorityScore'a göre sıralayın
        orders.sort((o1, o2) -> Double.compare(o2.getPriorityScore(), o1.getPriorityScore()));

        return orders;
    }




}