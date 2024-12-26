package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // Role'ü ROLE_ prefix'i ile birlikte ekle
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + customer.getRole().name()));

        return new User(customer.getUsername(), customer.getPassword(), authorities);
    }

    public Customer register(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        if (customer.getTotalSpent() == null) {
            customer.setTotalSpent(0.0);
        }
        if (customer.getBudget() == null) {
            customer.setBudget(1000.0);
        }
        if (customer.getCustomerType() == null) {
            customer.setCustomerType(Customer.CustomerType.STANDARD);
        }
        if (customer.getRole() == null) {
            customer.setRole(Customer.Role.USER);
        }

        return customerRepository.save(customer);
    }

    public Customer getCurrentCustomer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + username));
    }
}