package com.zemicon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zemicon.dto.ComparisonResult;
import com.zemicon.model.RequirementStatus;
import com.zemicon.service.ComparisonService;

@RestController
@RequestMapping("/api/comparison")
public class ComparisonController {

    private final ComparisonService service;

    public ComparisonController(ComparisonService service) {
        this.service = service;
    }

    // GET /api/comparison/{customerId} => returns only OPEN requirements
    @GetMapping("/{customerId}")
    public ResponseEntity<ComparisonResult> compare(@PathVariable Long customerId) {
        try {
            ComparisonResult result = service.compareByCustomer(customerId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // PUT /api/comparison/update-status/{requirementId}?status=CLOSED
    @PutMapping("/update-status/{requirementId}")
    public ResponseEntity<String> updateStatus(@PathVariable Long requirementId, @RequestParam String status) {
        try {
            RequirementStatus enumStatus = RequirementStatus.valueOf(status.toUpperCase());
            service.updateRfqStatus(requirementId, enumStatus);
            return ResponseEntity.ok("✅ Requirement " + requirementId + " updated to status " + enumStatus);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        }
    }
}
