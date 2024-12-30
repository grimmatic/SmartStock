package yazlab.smartstock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yazlab.smartstock.entity.Product;
import yazlab.smartstock.service.LogService;
import yazlab.smartstock.service.ProductService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final LogService logService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.save(product));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logService.logWarning("Ürün silme başarısız: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/upload")
    public ResponseEntity<?> createProductWithImage(
            @RequestParam("name") String name,
            @RequestParam("stock") Integer stock,
            @RequestParam("price") Double price,
            @RequestParam("image") MultipartFile image) {

        try {
            String fileName = name.toLowerCase()
                    .replace("ğ", "g")
                    .replace("ü", "u")
                    .replace("ş", "s")
                    .replace("ı", "i")
                    .replace("ö", "o")
                    .replace("ç", "c")
                    .replace(" ", "-")
                    .replaceAll("[^a-z0-9-]", "")
                    .trim() + ".png";

            // Kaynak klasörü yolu
            Resource resource = new ClassPathResource("static/images/products");
            Path uploadPath = Paths.get(resource.getFile().getAbsolutePath());

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Ürünü veritabanına kaydet
            Product product = new Product();
            product.setProductName(name);
            product.setStock(stock);
            product.setPrice(price);
            product.setImageUrl("/images/products/" + fileName);

            Product savedProduct = productService.save(product);

            return ResponseEntity.ok(savedProduct);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}