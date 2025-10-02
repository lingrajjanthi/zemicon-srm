package com.zemicon.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.zemicon.dto.VendorQuoteRequest;
import com.zemicon.model.Customer;
import com.zemicon.model.Requirement;
import com.zemicon.model.Vendor;
import com.zemicon.model.VendorResponse;
import com.zemicon.repo.CustomerRepository;
import com.zemicon.repo.RequirementRepository;
import com.zemicon.repo.VendorRepository;
import com.zemicon.repo.VendorResponseRepository;
import com.zemicon.service.VendorResponseService;
@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    @Autowired
    private VendorResponseService service;

    @Autowired
    private VendorRepository vendorRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private RequirementRepository requirementRepo;

    @Autowired
    private VendorResponseRepository responseRepo;

    // Bulk upload vendor Excel
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadVendorExcel(@RequestParam Long vendorId,
                                                    @RequestParam Long customerId,
                                                    @RequestPart("file") MultipartFile file) {
        service.bulkUploadExcel(file, vendorId, customerId);
        return ResponseEntity.ok("Vendor Excel uploaded successfully");
    }

    // Get vendor responses for vendor+customer
    @GetMapping("/quotes")
    public List<VendorResponse> getVendorQuotes(@RequestParam Long vendorId,
                                                @RequestParam Long customerId) {
        return service.getQuotesForVendorAndCustomer(vendorId, customerId);
    }

    // Get responses for a requirement
    @GetMapping("/responses/{requirementId}")
    public List<VendorResponse> getResponses(@PathVariable Long requirementId) {
        return service.getResponsesForRequirement(requirementId);
    }

	@PostMapping("/add-vendor")
	public ResponseEntity<?> addVendor(@RequestBody Vendor vendor) {
		return vendorRepo.findByName(vendor.getName())
				.<ResponseEntity<?>>map(existing -> ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Map.of("error", "Vendor already exists: " + existing.getName())))
				.orElseGet(() -> ResponseEntity.ok(vendorRepo.save(vendor)));
	}

    @GetMapping("/all")
    public List<Vendor> getAllVendors() {
        return vendorRepo.findAll();
    }

    @PostMapping("/add-quote")
    public ResponseEntity<?> addManualQuote(@RequestBody VendorQuoteRequest request) {
        Vendor vendor = vendorRepo.findByName(request.getVendorName())
                .orElseThrow(() -> new RuntimeException("Vendor not found: " + request.getVendorName()));

        Customer customer = customerRepo.findByName(request.getCustomerName())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + request.getCustomerName()));

        // ✅ Check requirement existence
        Requirement requirement = requirementRepo.findByCustomerAndMpn(customer, request.getMpn())
                .orElse(null);

        if (requirement == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "Requirement not found",
                        "message", "No requirement found for customer '" + customer.getName() + 
                                   "' and MPN '" + request.getMpn() + 
                                   "'. Please ensure RFQ is entered before adding vendor quote."
                    ));
        }

        VendorResponse response = new VendorResponse();
        response.setVendor(vendor);
        response.setRequirement(requirement);
        response.setUnitPrice(request.getUnitPrice());
        response.setLeadTimeWeeks(request.getLeadTimeWeeks());
        response.setMoq(request.getMoq());
        response.setDateCode(request.getDateCode());
        response.setRemarks(request.getRemarks());

        responseRepo.save(response);

        return ResponseEntity.ok("✅ Vendor quote added successfully for vendor: " + vendor.getName());
    }

}

