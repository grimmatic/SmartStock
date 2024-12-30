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

    // Yeni eklenen alan
    private String imageUrl;

    public String getImageFileName() {
        return this.productName
                .toLowerCase()
                .replace("ğ", "g")
                .replace("ü", "u")
                .replace("ş", "s")
                .replace("ı", "i")
                .replace("ö", "o")
                .replace("ç", "c")
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "") + ".png";
    }
}