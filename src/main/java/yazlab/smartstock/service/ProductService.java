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
        // Null kontrolü
        if (product.getStock() == null) {
            product.setStock(0);
        }
        if (product.getPrice() == null) {
            product.setPrice(0.0);
        }
        return productRepository.save(product);
    }

    public synchronized Product updateStock(Long id, Integer quantity) {
        Product product = findById(id);
        if (product.getStock() == null) {
            product.setStock(0);
        }
        product.setStock(product.getStock() + quantity);
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Ürün bulunamadı: ID = " + id);
        }
        productRepository.deleteById(id);
    }
}