package com.zemicon.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zemicon.dto.VendorQuote;
import com.zemicon.model.Customer;
import com.zemicon.repo.CustomerRepository;
import com.zemicon.service.VendorQuoteService;

@RestController
@RequestMapping("/api/customer-quotes")
public class CustomerQuoteController {

    @Autowired
    private VendorQuoteService quoteService;

    @GetMapping("/{customerId}")
    public Map<String, List<VendorQuote>> getCustomerQuotes(@PathVariable Long customerId) {
        return quoteService.getCustomerQuotes(customerId);
    }

    @GetMapping("/all-customers")
    public List<Customer> getAllCustomers() {
        // Return all customers for dropdown
        return customerRepo.findAll();
    }

    @Autowired
    private CustomerRepository customerRepo;
}
