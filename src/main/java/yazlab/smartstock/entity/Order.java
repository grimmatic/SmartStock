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
    @JoinColumn(name = "product_id", nullable = true, foreignKey = @ForeignKey(name = "fk_order_product"))
    private Product product;


    private Integer quantity;
    private Double totalPrice;
    private LocalDateTime orderDate;


    @Transient
    private Double priorityScore;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public enum OrderStatus {
        PENDING,    // Sipariş verildi, admin onayı bekliyor
        COMPLETED,  // Sipariş onaylandı ve tamamlandı
        CANCELLED  // Sipariş iptal edildi
    }


    // Dinamik PriorityScore Hesaplama
    public Double getPriorityScore() {
        double basePriority = customer.getCustomerType() == Customer.CustomerType.PREMIUM ? 15 : 10;

        // Bekleme Süresi (saniye cinsinden)
        long waitingTimeInSeconds = java.time.Duration.between(orderDate, LocalDateTime.now()).getSeconds();

        // Bekleme Süresi Ağırlığı
        double waitingWeight = 0.5;

        // Öncelik Skoru Hesaplama
        return basePriority + (waitingTimeInSeconds * waitingWeight);
    }

}