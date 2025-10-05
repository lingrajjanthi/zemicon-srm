package com.zemicon.controller;

import com.zemicon.dto.FinalQuoteItem;
import com.zemicon.dto.FinalQuoteRequest;
import com.zemicon.model.Customer;
import com.zemicon.model.Requirement;
import com.zemicon.repo.CustomerRepository;
import com.zemicon.repo.RequirementRepository;
import com.zemicon.service.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/final-quotes")
public class FinalQuoteController {

    @Autowired private CustomerRepository customerRepo;
    @Autowired private RequirementRepository requirementRepo;
    @Autowired private EmailService emailService;

    // Send consolidated final quote via email
    @PostMapping("/send")
    public ResponseEntity<String> sendFinalQuote(@RequestBody FinalQuoteRequest req) {
        Customer customer = customerRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        String email = (req.getEmail() != null && !req.getEmail().isEmpty())
                ? req.getEmail()
                : customer.getEmail();

        if (email == null) {
            return ResponseEntity.badRequest().body("No email available for this customer");
        }

        StringBuilder body = new StringBuilder("Dear " + customer.getName() + ",\n\n");
        body.append("Here is your consolidated quote:\n\n");

        for (FinalQuoteItem item : req.getItems()) {
            Requirement r = requirementRepo.findById(item.getRequirementId())
                    .orElseThrow(() -> new RuntimeException("Requirement not found"));
            body.append("- ")
                .append(r.getMpn()).append(" | Qty: ").append(r.getQuantity())
                .append(" | Final Quote: ₹").append(item.getFinalQuote()).append("\n");
        }
        body.append("\nRegards,\nZemicon Electronics");

        // You need EmailService implementation using JavaMailSender
        emailService.sendMail(email, "Final Quote - Zemicon", body.toString());

        return ResponseEntity.ok("Final quote sent to " + email);
    }

    // Export consolidated final quote as Excel
    @PostMapping("/export")
    public void exportFinalQuote(@RequestBody FinalQuoteRequest req, HttpServletResponse response) throws IOException {
        Customer customer = customerRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Final Quote");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Manufacturer");
        header.createCell(1).setCellValue("MPN");
        header.createCell(2).setCellValue("Qty");
        header.createCell(3).setCellValue("Final Quote");

        int rowIdx = 1;
        for (FinalQuoteItem item : req.getItems()) {
            Requirement r = requirementRepo.findById(item.getRequirementId())
                    .orElseThrow(() -> new RuntimeException("Requirement not found"));
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(r.getManufacturer());
            row.createCell(1).setCellValue(r.getMpn());
            row.createCell(2).setCellValue(r.getQuantity());
            row.createCell(3).setCellValue(item.getFinalQuote());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Final_Quote.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
