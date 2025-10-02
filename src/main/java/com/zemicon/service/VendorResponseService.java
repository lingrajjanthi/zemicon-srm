package com.zemicon.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zemicon.model.*;
import com.zemicon.repo.*;

@Service
public class VendorResponseService {

    @Autowired
    private VendorResponseRepository responseRepo;
    @Autowired
    private VendorRepository vendorRepo;
    @Autowired
    private RequirementRepository reqRepo;
    @Autowired
    private CustomerRepository customerRepo;

    // Bulk upload Excel for vendorId + customerId
    // expected columns (recommended): Manufacturer, MPN, Quantity, TargetPrice, Remarks, UnitPrice, LeadTimeWeeks, DateCode, MOQ
    public void bulkUploadExcel(MultipartFile file, Long vendorId, Long customerId) {
        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + vendorId));
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String manufacturer = getString(row, 0);
                String mpn = getString(row, 1);
                Integer quantity = getInteger(row, 2);
                Double targetPrice = getDouble(row, 3);
                String remarks = getString(row, 4);
                Double unitPrice = getDouble(row, 5);
                Integer leadTime = getInteger(row, 6);
                String dateCode = getString(row, 7);
                Integer moq = getInteger(row, 8);

                if (mpn == null || mpn.isBlank()) continue;
                String mpnKey = mpn.trim();

                // find requirement by customerId + mpn
                Optional<Requirement> reqOpt = reqRepo.findByCustomer_IdAndMpn(customerId, mpnKey);
                Requirement req = reqOpt.orElseGet(() -> {
                    // create requirement if missing so vendor responses link
                    Requirement newReq = new Requirement();
                    newReq.setCustomer(customer);
                    newReq.setManufacturer(manufacturer);
                    newReq.setMpn(mpnKey);
                    newReq.setQuantity(quantity);
                    newReq.setTargetPrice(targetPrice);
                    newReq.setRemarks(remarks);
                    return reqRepo.save(newReq);
                });

                // create or update VendorResponse
                Optional<VendorResponse> existing = responseRepo.findByRequirementAndVendor(req, vendor);
                VendorResponse vr = existing.orElseGet(VendorResponse::new);
                vr.setRequirement(req);
                vr.setVendor(vendor);
                vr.setUnitPrice(unitPrice);
                vr.setLeadTimeWeeks(leadTime);
                vr.setDateCode(dateCode);
                vr.setMoq(moq);
                vr.setRemarks(remarks);
                vr.setSubmittedAt(LocalDateTime.now());
                responseRepo.save(vr);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to process Excel file", e);
        }
    }

    // helper readers
    private String getString(Row r, int idx) {
        if (r.getCell(idx) == null) return null;
        r.getCell(idx).setCellType(CellType.STRING);
        return r.getCell(idx).getStringCellValue().trim();
    }
    private Double getDouble(Row r, int idx) {
        if (r.getCell(idx) == null) return null;
        if (r.getCell(idx).getCellType() == CellType.NUMERIC) return r.getCell(idx).getNumericCellValue();
        try { return Double.parseDouble(getString(r, idx)); } catch (Exception ex) { return null; }
    }
    private Integer getInteger(Row r, int idx) {
        Double d = getDouble(r, idx);
        return d == null ? null : d.intValue();
    }

    // Get vendor responses for a requirement
    public List<VendorResponse> getResponsesForRequirement(Long requirementId) {
        Requirement req = reqRepo.findById(requirementId)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
        return responseRepo.findByRequirement(req);
    }

    // View vendor quotes for vendor+customer (latest first)
    public List<VendorResponse> getQuotesForVendorAndCustomer(Long vendorId, Long customerId) {
        return responseRepo.findByRequirement_Customer_IdAndVendor_IdOrderBySubmittedAtDesc(customerId, vendorId);
    }
}
