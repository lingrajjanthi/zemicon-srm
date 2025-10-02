//package com.zemicon.service;
//
//import com.zemicon.dto.*;
//import com.zemicon.model.*;
//import com.zemicon.repo.CustomerRepository;
//import com.zemicon.repo.RequirementRepository;
//import com.zemicon.repository.*;
//
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class ComparisonService {
//
//    private final RequirementRepository requirementRepo;
//    private final VendorQuoteRepository vendorQuoteRepo;
//    private final CustomerRepository customerRepo;
//
//    public ComparisonService(RequirementRepository requirementRepo,
//                             VendorQuoteRepository vendorQuoteRepo,
//                             CustomerRepository customerRepo) {
//        this.requirementRepo = requirementRepo;
//        this.vendorQuoteRepo = vendorQuoteRepo;
//        this.customerRepo = customerRepo;
//    }
//
//    public ComparisonResult compareByCustomer(Long customerId) {
//        var customer = customerRepo.findById(customerId)
//                .orElseThrow(() -> new RuntimeException("Customer not found"));
//
//        var requirements = requirementRepo.findByCustomerId(customer);
//        var result = new ComparisonResult();
//        result.setCustomerId(customerId);
//        result.setCustomerName(customer.getName());
//
//        List<ComparisonRow> rows = new ArrayList<>();
//
//        for (Requirement req : requirements) {
//            ComparisonRow row = new ComparisonRow();
//            row.setManufacturer(req.getManufacturer());
//            row.setMpn(req.getMpn());
//            row.setQuantity(req.getQuantity());
//            row.setTargetPrice(req.getTargetPrice());
//
//            // Fetch vendor quotes for this RFQ ID
//            List<VendorQuoteEntity> entities = vendorQuoteRepo.findByRfqId(req.getRfqId());
//
//            List<VendorQuote> quotes = entities.stream().map(e -> {
//                VendorQuote vq = new VendorQuote();
//                vq.setVendorName(e.getVendor().getName());
//                vq.setUnitPrice(e.getUnitPrice());
//                vq.setLeadTime(e.getLeadTime());
//                vq.setDateCode(e.getDateCode());
//                vq.setMoq(e.getMoq());
//                vq.setRemarks(e.getRemarks());
//                return vq;
//            }).collect(Collectors.toList());
//
//            row.setVendorQuotes(quotes);
//
//            // --- Find L1 and L2 ---
//            List<VendorQuote> sorted = new ArrayList<>(quotes);
//            sorted.sort(Comparator
//                    .comparing(VendorQuote::getUnitPrice, Comparator.nullsLast(Double::compare))
//                    .thenComparing(VendorQuote::getLeadTime, Comparator.nullsLast(Integer::compare)));
//
//            if (!sorted.isEmpty()) row.setL1Vendor(sorted.get(0));
//            if (sorted.size() > 1) row.setL2Vendor(sorted.get(1));
//
//            rows.add(row);
//        }
//
//        result.setRows(rows);
//        return result;
//    }
//}
