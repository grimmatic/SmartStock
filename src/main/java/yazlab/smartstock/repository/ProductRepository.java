package yazlab.smartstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yazlab.smartstock.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
