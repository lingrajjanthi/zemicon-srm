package com.zemicon.repo;

import com.zemicon.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByName(String name);
    Optional<Customer> findByCustomerCode(String code);
    boolean existsByEmail(String email);
}
