package com.zemicon.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zemicon.dto.VendorQuoteDto;
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

    @Autowired private VendorResponseService service;
    @Autowired private VendorRepository vendorRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private RequirementRepository requirementRepo;
    @Autowired private VendorResponseRepository responseRepo;

    // ✅ Upload vendor Excel
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadVendorExcel(
            @RequestParam Long vendorId,
            @RequestParam Long customerId,
            @RequestPart("file") MultipartFile file) {

        int saved = service.bulkUploadExcel(file, vendorId, customerId);
        return ResponseEntity.ok("✅ Saved " + saved + " vendor quotes successfully.");
    }

    // ✅ Get responses for a requirement
    @GetMapping("/responses/{requirementId}")
    public List<VendorResponse> getResponses(@PathVariable Long requirementId) {
        return service.getResponsesForRequirement(requirementId);
    }

    // ✅ Add vendor (with duplicate name check)
    @PostMapping("/add-vendor")
    public ResponseEntity<?> addVendor(@RequestBody Vendor vendor) {
        return vendorRepo.findByName(vendor.getName())
                .<ResponseEntity<?>>map(existing -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Vendor already exists: " + existing.getName())))
                .orElseGet(() -> ResponseEntity.ok(vendorRepo.save(vendor)));
    }

    // ✅ Get all vendors
    @GetMapping("/all")
    public List<Vendor> getAllVendors() {
        return vendorRepo.findAll();
    }

    // ✅ Add manual vendor quote
    @PostMapping("/add-quote")
    public ResponseEntity<?> addManualQuote(@RequestBody VendorQuoteRequest request) {
        Vendor vendor = vendorRepo.findByName(request.getVendorName())
                .orElseThrow(() -> new RuntimeException("Vendor not found: " + request.getVendorName()));

        Customer customer = customerRepo.findByName(request.getCustomerName())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + request.getCustomerName()));

        Requirement requirement = requirementRepo.findByCustomerAndMpn(customer, request.getMpn())
                .orElse(null);

        if (requirement == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Requirement not found",
                            "message", "No requirement found for customer '" + customer.getName()
                                    + "' and MPN '" + request.getMpn()
                                    + "'. Please ensure RFQ is entered before adding vendor quote."));
        }

        VendorResponse response = new VendorResponse();
        response.setVendor(vendor);
        response.setCustomer(customer);
        response.setRequirement(requirement);
        response.setUnitPrice(request.getUnitPrice());
        response.setLeadTimeWeeks(request.getLeadTimeWeeks());
        response.setMoq(request.getMoq());
        response.setDateCode(request.getDateCode());
        response.setRemarks(request.getRemarks());

        responseRepo.save(response);
        return ResponseEntity.ok("✅ Vendor quote added successfully for vendor: " + vendor.getName());
    }

    // ✅ Get latest vendor quotes by vendor & customer
    @GetMapping("/quotes")
    public ResponseEntity<List<VendorQuoteDto>> getQuotes(@RequestParam Long vendorId,
                                                          @RequestParam Long customerId) {
        List<VendorResponse> list =
                responseRepo.findByRequirement_Customer_IdAndVendor_IdOrderBySubmittedAtDesc(customerId, vendorId);

        List<VendorQuoteDto> dtos = list.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ✅ Mapper
    private VendorQuoteDto toDto(VendorResponse r) {
        VendorQuoteDto d = new VendorQuoteDto();
        d.id = r.getId();
        d.manufacturer = r.getManufacturer();
        d.mpn = r.getMpn();
        d.quantity = r.getQuantity();
        d.unitPrice = r.getUnitPrice();
        d.leadTimeWeeks = r.getLeadTimeWeeks();
        d.moq = r.getMoq();
        d.dateCode = r.getDateCode();
        d.remarks = r.getRemarks();
        d.submittedAt = r.getSubmittedAt();

        if (r.getRequirement() != null) {
            VendorQuoteDto.Req rq = new VendorQuoteDto.Req();
            rq.id = r.getRequirement().getId();
            rq.mpn = r.getRequirement().getMpn();
            rq.manufacturer = r.getRequirement().getManufacturer();
            rq.quantity = r.getRequirement().getQuantity();
            d.requirement = rq;
        }
        return d;
    }
}
