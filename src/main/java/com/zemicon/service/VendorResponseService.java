package com.zemicon.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zemicon.model.Customer;
import com.zemicon.model.Requirement;
import com.zemicon.model.Vendor;
import com.zemicon.model.VendorResponse;
import com.zemicon.repo.CustomerRepository;
import com.zemicon.repo.RequirementRepository;
import com.zemicon.repo.VendorRepository;
import com.zemicon.repo.VendorResponseRepository;

@Service
public class VendorResponseService {

    @Autowired private VendorResponseRepository responseRepo;
    @Autowired private VendorRepository vendorRepo;
    @Autowired private RequirementRepository reqRepo;
    @Autowired private CustomerRepository customerRepo;

    // ✅ Fetch all responses for a specific requirement
    public List<VendorResponse> getResponsesForRequirement(Long requirementId) {
        Requirement req = reqRepo.findById(requirementId)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
        return responseRepo.findByRequirement(req);
    }

    // ✅ Upload vendor Excel and save responses
    public int bulkUploadExcel(MultipartFile file, Long vendorId, Long customerId) {
        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + vendorId));
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        int savedCount = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // ✅ Correct Excel mapping
                String manufacturer = getString(row, 0);
                String mpn = getString(row, 1);
                Integer quantity = getInteger(row, 2);
                Double unitPrice = getDouble(row, 3);
                Integer leadTimeWeeks = getInteger(row, 4);
                Integer moq = getInteger(row, 5);
                String dateCode = getString(row, 6);
                String remarks = getString(row, 7);

                if (mpn == null || mpn.isBlank()) continue;
                String normalizedMpn = mpn.trim().toUpperCase();

                // ✅ Find or create Requirement
                Requirement req = reqRepo.findByCustomerAndMpn(customer, normalizedMpn)
                        .orElseGet(() -> {
                            Requirement newReq = new Requirement();
                            newReq.setCustomer(customer);
                            newReq.setManufacturer(manufacturer);
                            newReq.setMpn(normalizedMpn);
                            newReq.setQuantity(quantity);
                            newReq.setTargetPrice(0.0);
                            newReq.setRemarks("Auto-created from vendor upload");
                            return reqRepo.save(newReq);
                        });

                // ✅ Create or update VendorResponse
                VendorResponse vr = responseRepo.findByRequirementAndVendor(req, vendor)
                        .orElse(new VendorResponse());

                vr.setRequirement(req);
                vr.setCustomer(customer); // Important
                vr.setVendor(vendor);
                vr.setManufacturer(manufacturer);
                vr.setMpn(normalizedMpn);
                vr.setQuantity(quantity);
                vr.setUnitPrice(unitPrice);
                vr.setLeadTimeWeeks(leadTimeWeeks);
                vr.setMoq(moq);
                vr.setDateCode(dateCode);
                vr.setRemarks(remarks);
                vr.setSubmittedAt(LocalDateTime.now());

                responseRepo.save(vr);
                savedCount++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process Excel file", e);
        }

        return savedCount;
    }

    // Utility methods for Excel parsing
    private String getString(Row r, int idx) {
        if (r.getCell(idx) == null) return null;
        r.getCell(idx).setCellType(CellType.STRING);
        return r.getCell(idx).getStringCellValue().trim();
    }

    private Double getDouble(Row r, int idx) {
        if (r.getCell(idx) == null) return null;
        if (r.getCell(idx).getCellType() == CellType.NUMERIC)
            return r.getCell(idx).getNumericCellValue();
        try { return Double.parseDouble(getString(r, idx)); } catch (Exception ex) { return null; }
    }

    private Integer getInteger(Row r, int idx) {
        Double d = getDouble(r, idx);
        return d == null ? null : d.intValue();
    }
}


