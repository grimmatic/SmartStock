package yazlab.smartstock.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "products")
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {
    private String productName;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false)
    private Double price = 0.0;

}