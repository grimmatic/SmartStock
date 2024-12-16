package yazlab.smartstock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yazlab.smartstock.entity.Customer;
import yazlab.smartstock.repository.CustomerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı"));
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
}
