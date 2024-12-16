package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yazlab.smartstock.entity.Product;
import yazlab.smartstock.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public synchronized Product updateStock(Long id, Integer quantity) {
        Product product = findById(id);
        product.setStock(product.getStock() + quantity);
        return productRepository.save(product);
    }
}