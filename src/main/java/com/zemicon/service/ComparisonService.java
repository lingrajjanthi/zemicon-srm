package com.zemicon.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.zemicon.dto.ComparisonResult;
import com.zemicon.dto.ComparisonRow;
import com.zemicon.dto.VendorQuote;
import com.zemicon.model.Customer;
import com.zemicon.model.Requirement;
import com.zemicon.model.RequirementStatus;
import com.zemicon.model.VendorResponse;
import com.zemicon.repo.CustomerRepository;
import com.zemicon.repo.RequirementRepository;
import com.zemicon.repo.VendorResponseRepository;
@Service
public class ComparisonService {

    private final RequirementRepository requirementRepo;
    private final VendorResponseRepository vendorResponseRepo;
    private final CustomerRepository customerRepo;

    public ComparisonService(RequirementRepository requirementRepo,
                             VendorResponseRepository vendorResponseRepo,
                             CustomerRepository customerRepo) {
        this.requirementRepo = requirementRepo;
        this.vendorResponseRepo = vendorResponseRepo;
        this.customerRepo = customerRepo;
    }

    public ComparisonResult compareByCustomer(Long customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // ✅ Fetch only OPEN requirements
        List<Requirement> requirements = requirementRepo.findByCustomerAndStatus(customer, RequirementStatus.OPEN);

        ComparisonResult result = new ComparisonResult();
        result.setCustomerId(customerId);
        result.setCustomerName(customer.getName());

        List<ComparisonRow> rows = new ArrayList<>();

        for (Requirement req : requirements) {
            ComparisonRow row = new ComparisonRow();
            row.setRequirementId(req.getId());
            row.setManufacturer(req.getManufacturer());
            row.setMpn(req.getMpn());
            row.setQuantity(req.getQuantity());
            row.setTargetPrice(req.getTargetPrice());

            // fetch all vendor responses for this requirement
            List<VendorResponse> responses = vendorResponseRepo.findByRequirement(req);

            List<VendorQuote> quotes = responses.stream().map(r -> {
                VendorQuote vq = new VendorQuote();
                vq.setVendorName(r.getVendor().getName());
                vq.setUnitPrice(r.getUnitPrice());
                vq.setLeadTimeWeeks(r.getLeadTimeWeeks());
                vq.setDateCode(r.getDateCode());
                vq.setMoq(r.getMoq());
                vq.setRemarks(r.getRemarks());
                vq.setSubmittedAt(r.getSubmittedAt());
                return vq;
            }).collect(Collectors.toList());

            // sort quotes by unit price first, then lead time
            quotes.sort(
                Comparator.comparing(VendorQuote::getUnitPrice, Comparator.nullsLast(Double::compare))
                          .thenComparing(VendorQuote::getLeadTimeWeeks, Comparator.nullsLast(Integer::compare))
            );

            row.setVendorQuotes(quotes);
            if (!quotes.isEmpty()) {
                row.setBestVendor(quotes.get(0).getVendorName());
            }

            rows.add(row);
        }

        result.setRows(rows);
        return result;
    }

    // update RFQ status
    public void updateRfqStatus(Long requirementId, RequirementStatus status) {
        Requirement req = requirementRepo.findById(requirementId)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
        req.setStatus(status);
        if (status == RequirementStatus.CLOSED) {
            req.setClosedAt(java.time.LocalDateTime.now());
        }
        requirementRepo.save(req);
    }
}



