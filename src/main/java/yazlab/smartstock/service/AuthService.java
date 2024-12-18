package yazlab.smartstock.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yazlab.smartstock.entity.Customer;
import yazlab.smartstock.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;

    public Customer register(Customer customer) {
        try {
            log.info("Registration attempt for username: {}", customer.getUsername());

            // Check if username already exists
            if (customerRepository.findByUsername(customer.getUsername()).isPresent()) {
                throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor");
            }

            // Encode password
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));

            // Set initial values
            customer.setTotalSpent(0.0);
            if (customer.getBudget() == null) {
                customer.setBudget(1000.0); // Default budget
            }
            if (customer.getCustomerType() == null) {
                customer.setCustomerType(Customer.CustomerType.STANDARD); // Default type
            }

            Customer savedCustomer = customerRepository.save(customer);
            log.info("Registration successful for username: {}", customer.getUsername());

            return savedCustomer;
        } catch (Exception e) {
            log.error("Registration failed for username: {}", customer.getUsername(), e);
            throw e;
        }
    }

    public Customer login(String username, String password) {
        try {
            log.info("Login attempt for username: {}", username);

            Customer customer = customerRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            if (!passwordEncoder.matches(password, customer.getPassword())) {
                log.error("Invalid password for username: {}", username);
                throw new RuntimeException("Hatalı şifre");
            }

            session.setAttribute("currentCustomer", customer);
            log.info("Login successful for username: {}", username);

            return customer;
        } catch (Exception e) {
            log.error("Login failed for username: {}", username, e);
            throw e;
        }
    }
}