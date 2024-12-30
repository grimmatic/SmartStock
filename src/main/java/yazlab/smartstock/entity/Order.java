package yazlab.smartstock.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private Double totalPrice;
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private Double priorityScore = 0.0;  // varsayılan değer ekleyelim

    @Column(nullable = false)
    private LocalDateTime lastPriorityUpdateTime;


    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public enum OrderStatus {
        PENDING,    // Sipariş verildi, admin onayı bekliyor
        COMPLETED,  // Sipariş onaylandı ve tamamlandı
        CANCELLED  // Sipariş iptal edildi
    }


    public Double calculateCurrentPriorityScore() {
        double basePriority = customer.getCustomerType() == Customer.CustomerType.PREMIUM ? 15 : 10;

        if (lastPriorityUpdateTime == null) {
            lastPriorityUpdateTime = orderDate;  // orderDate kullanıyoruz
            return basePriority;
        }

        // Sipariş tarihinden itibaren geçen toplam süreyi hesapla
        long totalWaitingTimeInSeconds = java.time.Duration.between(orderDate, LocalDateTime.now()).getSeconds();

        return basePriority + (totalWaitingTimeInSeconds * 0.5);
    }


    public void prePersist() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (lastPriorityUpdateTime == null) {
            lastPriorityUpdateTime = orderDate;
        }
        if (priorityScore == null || priorityScore == 0.0) {
            priorityScore = customer.getCustomerType() == Customer.CustomerType.PREMIUM ? 15.0 : 10.0;
        }
    }

}