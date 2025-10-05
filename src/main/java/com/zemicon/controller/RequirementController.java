package com.zemicon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.zemicon.model.Customer;
import com.zemicon.model.Requirement;
import com.zemicon.model.RequirementStatus;
import com.zemicon.service.RequirementService;
import com.zemicon.repo.CustomerRepository;

@RestController
@RequestMapping("/api/requirements")
public class RequirementController {

    @Autowired
    private RequirementService requirementService;

    @Autowired
    private CustomerRepository customerRepo;

    // Upload RFQ Excel
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadRFQ(@RequestParam String customerName,
                                            @RequestPart("file") MultipartFile file) {
        Customer customer = customerRepo.findByName(customerName)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerName));

        requirementService.saveFromExcel(customer, file);
        return ResponseEntity.ok("RFQ uploaded successfully for customer: " + customerName);
    }

    // Get all RFQs for customer
    @GetMapping("/by-customer/{customerName}")
    public List<Requirement> getByCustomerName(
            @PathVariable String customerName,
            @RequestParam(required = false) String status) {
        Customer customer = customerRepo.findByName(customerName)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerName));

        if (status != null && !status.isBlank()) {
            RequirementStatus rs = RequirementStatus.valueOf(status.toUpperCase());
            return requirementService.getByCustomerAndStatus(customer, rs);
        }
        return requirementService.getByCustomerId(customer.getId());
    }


    // Add manual RFQ
    @PostMapping("/add")
    public ResponseEntity<String> addManualRFQ(@RequestParam String customerName,
                                               @RequestBody Requirement req) {
        Customer customer = customerRepo.findByName(customerName)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerName));

        req.setCustomer(customer);

        // Default to NEW if no status provided
        if (req.getStatus() == null) {
            req.setStatus(RequirementStatus.NEW);
        }

        requirementService.saveRequirement(req);

        return ResponseEntity.ok("RFQ added manually for customer: " + customerName);
    }

    // Update RFQ status (e.g., close it)
    @PostMapping("/update-status/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Requirement req = requirementService.getById(id)
                .orElseThrow(() -> new RuntimeException("Requirement not found for ID: " + id));

        try {
            RequirementStatus rs = RequirementStatus.valueOf(status.toUpperCase());
            req.setStatus(rs);

            if (rs == RequirementStatus.CLOSED) {
                req.setClosedAt(java.time.LocalDateTime.now());
            }

            requirementService.saveRequirement(req);
            return ResponseEntity.ok("RFQ status updated to " + rs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value. Allowed: NEW, OPEN, PENDING, CLOSED");
        }
    }
}
