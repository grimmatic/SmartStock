package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yazlab.smartstock.entity.Customer;
import yazlab.smartstock.repository.CustomerRepository;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        return new User(customer.getUsername(), customer.getPassword(), new ArrayList<>());
    }

    public Customer register(Customer customer) {
        // Şifreyi hashle
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        // Varsayılan başlangıç değerlerini ayarla
        if (customer.getTotalSpent() == null) {
            customer.setTotalSpent(0.0); // Default total spent
        }
        if (customer.getBudget() == null) {
            customer.setBudget(1000.0); // Default budget
        }
        if (customer.getCustomerType() == null) {
            customer.setCustomerType(Customer.CustomerType.STANDARD); // Default customer type
        }

        return customerRepository.save(customer);
    }

    public Customer getCurrentCustomer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + username));
    }
}
