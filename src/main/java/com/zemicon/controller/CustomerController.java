package com.zemicon.controller;

import com.zemicon.model.Customer;
import com.zemicon.repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerRepository repo;

    // Register new customer
 // Instead of UUID, generate simple incremental code
    @PostMapping("/add")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        if (repo.existsByEmail(customer.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered!");
        }

        long count = repo.count() + 1; // next number
        customer.setCustomerCode(String.format("CUST%03d", count)); // CUST001, CUST002, ...

        Customer saved = repo.save(customer);
        return ResponseEntity.ok(saved);
    }

    // Get all customers
    @GetMapping("/all")
    public List<Customer> getAll() {
        return repo.findAll();
    }

    // Get customer by code
    @GetMapping("/{code}")
    public ResponseEntity<Customer> getByCode(@PathVariable String code) {
        return repo.findByCustomerCode(code)
                   .map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return repo.findAll()
                .stream()
                .map(c -> new CustomerDTO(c.getId(), c.getName()))
                .toList();
    }

    // DTO so frontend only sees id + name
    public record CustomerDTO(Long id, String name) {}
}
