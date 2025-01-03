package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yazlab.smartstock.entity.Order;
import yazlab.smartstock.entity.Product;
import yazlab.smartstock.repository.OrderRepository;
import yazlab.smartstock.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final LogService logService;
    private final OrderRepository orderRepository;

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
        Product savedProduct = productRepository.save(product);
        logService.logProductAction("Değişim", savedProduct); // Loglama
        return savedProduct;
    }

    public synchronized Product updateStock(Long id, Integer quantity) {
        Product product = findById(id);
        if (product.getStock() == null) {
            product.setStock(0);
        }
        product.setStock(product.getStock() + quantity);
        Product updatedProduct = productRepository.save(product);
        String action = quantity > 0 ? "Ekleme" : "Eksiltme";
        logService.logProductAction(action, updatedProduct);
        return updatedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));


        List<Order> orders = orderRepository.findByProductId(id);
        orderRepository.deleteAll(orders);


        productRepository.deleteById(id);
        logService.logInfo("Ürün ve ilişkili siparişleri silindi: " + product.getProductName());
    }
}