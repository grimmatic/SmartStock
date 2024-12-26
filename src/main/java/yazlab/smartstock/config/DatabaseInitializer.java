package yazlab.smartstock.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import yazlab.smartstock.entity.Customer;
import yazlab.smartstock.entity.Product;
import yazlab.smartstock.repository.CustomerRepository;
import yazlab.smartstock.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeCustomers();
        initializeAdmin();
        initializeProducts();
    }

    private void initializeCustomers() {
        if (customerRepository.count() == 0) {
            Random random = new Random();
            // Premium müşteriler
            List<Customer> premiumCustomers = Arrays.asList(
                    createCustomer("premium1", "Premium User One", "premium1@example.com", "password123",
                            Customer.CustomerType.PREMIUM, 2000.0 + random.nextDouble() * 1000),
                    createCustomer("premium2", "Premium User Two", "premium2@example.com", "password123",
                            Customer.CustomerType.PREMIUM, 2000.0 + random.nextDouble() * 1000)
            );

            // Standard müşteriler
            List<Customer> standardCustomers = Arrays.asList(
                    createCustomer("user1", "Standard User One", "user1@example.com", "password123",
                            Customer.CustomerType.STANDARD, 500.0 + random.nextDouble() * 2500),
                    createCustomer("user2", "Standard User Two", "user2@example.com", "password123",
                            Customer.CustomerType.STANDARD, 500.0 + random.nextDouble() * 2500),
                    createCustomer("user3", "Standard User Three", "user3@example.com", "password123",
                            Customer.CustomerType.STANDARD, 500.0 + random.nextDouble() * 2500)
            );

            customerRepository.saveAll(premiumCustomers);
            customerRepository.saveAll(standardCustomers);
            System.out.println("Test müşterileri başarıyla oluşturuldu.");
        }
    }

    private void initializeAdmin() {
        if (customerRepository.findByUsername("admin").isEmpty()) {
            Customer admin = new Customer();
            admin.setUsername("admin");
            admin.setCustomerName("System Admin");
            admin.setEmail("admin@smartstock.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Customer.Role.ADMIN);
            admin.setCustomerType(Customer.CustomerType.PREMIUM); // Admin'i premium yaptık
            admin.setBudget(999999.0); // Admin için yüksek bütçe
            admin.setTotalSpent(0.0);
            customerRepository.save(admin);
            System.out.println("Admin kullanıcısı başarıyla oluşturuldu.");
        }
    }

    private void initializeProducts() {
        if (productRepository.count() == 0) {
            List<Product> products = Arrays.asList(
                    createProduct("Gaming Laptop", 12999.99, 50),
                    createProduct("Akıllı Telefon", 8499.99, 100),
                    createProduct("Kablosuz Kulaklık", 1299.99, 200),
                    createProduct("4K Monitor", 5999.99, 30),
                    createProduct("Mekanik Klavye", 899.99, 150),
                    createProduct("Oyuncu Mouse", 599.99, 250)
            );

            productRepository.saveAll(products);
            System.out.println("Test ürünleri başarıyla oluşturuldu.");
        }
    }

    private Customer createCustomer(String username, String customerName, String email,
                                    String password, Customer.CustomerType type, Double budget) {
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setCustomerName(customerName);
        customer.setEmail(email);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setCustomerType(type);
        customer.setBudget(budget);
        customer.setTotalSpent(0.0);
        return customer;
    }

    private Product createProduct(String name, Double price, Integer stock) {
        Product product = new Product();
        product.setProductName(name);
        product.setPrice(price);
        product.setStock(stock);
        return product;
    }
}