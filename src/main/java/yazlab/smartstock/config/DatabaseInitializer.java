package yazlab.smartstock.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import yazlab.smartstock.entity.Customer;
import yazlab.smartstock.entity.Product;
import yazlab.smartstock.repository.CustomerRepository;
import yazlab.smartstock.repository.ProductRepository;

import java.util.ArrayList;
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
            List<Customer> customers = new ArrayList<>();

            // Toplam müşteri sayısını random belirle (5-10 arası)
            int totalCustomers = random.nextInt(6) + 5; // 5 ile 10 arası

            // Premium müşteri sayısını random belirle (2-4 arası)
            int premiumCount = random.nextInt(3) + 2; // 2 ile 4 arası

            // Premium müşterileri oluştur
            for (int i = 1; i <= premiumCount; i++) {
                customers.add(createCustomer(
                        "premium" + i,
                        "Premium User " + i,
                        "premium" + i + "@example.com",
                        "password123",
                        Customer.CustomerType.PREMIUM,
                        getRandomBudget(random)
                ));
            }

            // Kalan müşterileri normal olarak oluştur
            for (int i = 1; i <= totalCustomers - premiumCount; i++) {
                customers.add(createCustomer(
                        "user" + i,
                        "Standard User " + i,
                        "user" + i + "@example.com",
                        "password123",
                        Customer.CustomerType.STANDARD,
                        getRandomBudget(random)
                ));
            }

            customerRepository.saveAll(customers);
            System.out.println(totalCustomers + " adet test müşterisi başarıyla oluşturuldu.");
        }
    }

    private double getRandomBudget(Random random) {
        // 500-3000 TL arası random bütçe
        return 500 + (random.nextDouble() * 2500);
    }

    private void initializeAdmin() {
        if (customerRepository.findByUsername("admin").isEmpty()) {
            Customer admin = new Customer();
            admin.setUsername("admin");
            admin.setCustomerName("System Admin");
            admin.setEmail("admin@smartstock.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Customer.Role.ADMIN);
            admin.setCustomerType(Customer.CustomerType.PREMIUM);
            admin.setBudget(999999.0);
            admin.setTotalSpent(0.0);
            customerRepository.save(admin);
            System.out.println("Admin kullanıcısı başarıyla oluşturuldu.");
        }
    }

    private void initializeProducts() {
        if (productRepository.count() == 0) {
            List<Product> products = Arrays.asList(
                    createProduct("Kahve", 45.99, 150),
                    createProduct("Çay (1 kg)", 89.99, 200),
                    createProduct("Su (5 lt)", 25.99, 300),
                    createProduct("Bisküvi Paketi", 15.99, 250),
                    createProduct("Meyve Suyu (1 lt)", 29.99, 180),
                    createProduct("Çikolata", 12.99, 400),
                    createProduct("Cips", 22.99, 350),
                    createProduct("Ekmek", 7.99, 500),
                    createProduct("Süt (1 lt)", 19.99, 200),
                    createProduct("Yoğurt (1 kg)", 35.99, 150)
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
        customer.setRole(Customer.Role.USER);
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