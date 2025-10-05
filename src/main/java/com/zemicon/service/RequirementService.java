package com.zemicon.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zemicon.model.Customer;
import com.zemicon.model.Requirement;
import com.zemicon.model.RequirementStatus;
import com.zemicon.repo.RequirementRepository;

@Service
public class RequirementService {

	@Autowired
	private RequirementRepository repo;

	// Excel upload
	public void saveFromExcel(Customer customer, MultipartFile file) {
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				String manufacturer = getString(row, 0);
				String mpn = getString(row, 1);
				Integer quantity = getInteger(row, 2);
				Double targetPrice = getDouble(row, 3);
				String remarks = getString(row, 4);

				if (mpn == null || mpn.isBlank())
					continue;
				String mpnKey = mpn.trim();

				repo.findByCustomerAndMpn(customer, mpnKey).ifPresentOrElse(existing -> {
					existing.setManufacturer(manufacturer);
					existing.setQuantity(quantity);
					existing.setTargetPrice(targetPrice);
					existing.setRemarks(remarks);
					existing.setStatus(RequirementStatus.OPEN); // reopen if updated
					repo.save(existing);
				}, () -> {
					Requirement r = new Requirement();
					r.setCustomer(customer);
					r.setManufacturer(manufacturer);
					r.setMpn(mpnKey);
					r.setQuantity(quantity);
					r.setTargetPrice(targetPrice);
					r.setRemarks(remarks);
					r.setStatus(RequirementStatus.NEW);
					repo.save(r);
				});
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to process RFQ Excel", e);
		}
	}

	public Requirement saveRequirement(Requirement req) {
		req.setStatus(RequirementStatus.NEW);
		req.setCreatedAt(LocalDateTime.now());
		return repo.save(req);
	}

	public List<Requirement> getOpenRequirements(Customer customer) {
		return repo.findByCustomerAndStatus(customer, RequirementStatus.OPEN);
	}

	public Requirement closeRequirement(Long requirementId) {
		Requirement r = repo.findById(requirementId).orElseThrow(() -> new RuntimeException("Requirement not found"));
		r.setStatus(RequirementStatus.CLOSED);
		r.setClosedAt(LocalDateTime.now());
		return repo.save(r);
	}

	// helper methods getString/getInteger/getDouble as before...

	public List<Requirement> getByCustomerId(Long customerId) {
		return repo.findByCustomer_IdOrderByCreatedAtDesc(customerId);
	}

	// small helpers to guard nulls
	private String getString(Row row, int idx) {
		if (row.getCell(idx) == null)
			return null;
		row.getCell(idx).setCellType(CellType.STRING);
		return row.getCell(idx).getStringCellValue().trim();
	}

	private Integer getInteger(Row row, int idx) {
		if (row.getCell(idx) == null)
			return null;
		if (row.getCell(idx).getCellType() == CellType.NUMERIC) {
			return (int) row.getCell(idx).getNumericCellValue();
		}
		try {
			return Integer.parseInt(getString(row, idx));
		} catch (Exception e) {
			return null;
		}
	}

	private Double getDouble(Row row, int idx) {
		if (row.getCell(idx) == null)
			return null;
		if (row.getCell(idx).getCellType() == CellType.NUMERIC) {
			return row.getCell(idx).getNumericCellValue();
		}
		try {
			return Double.parseDouble(getString(row, idx));
		} catch (Exception e) {
			return null;
		}
	}
	public java.util.Optional<Requirement> getById(Long id) {
	    return repo.findById(id);
	}
	
	public List<Requirement> getByCustomerAndStatus(Customer customer, RequirementStatus status) {
	    return repo.findByCustomerAndStatus(customer, status);
	}

}
