package yazlab.smartstock.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "customers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {
    private String customerName;
    private Double budget;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    private Double totalSpent;

    public enum CustomerType {
        PREMIUM, STANDARD
    }
    @Column(unique = true)
    private String username;
    private String password;
    private String email;
}