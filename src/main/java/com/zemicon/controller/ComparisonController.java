package com.zemicon.controller;

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

    @GetMapping("/{customerId}")
    public ComparisonResult compare(@PathVariable Long customerId) {
        return service.compareByCustomer(customerId);
    }

    @PutMapping("/update-status/{requirementId}")
    public String updateStatus(@PathVariable Long requirementId, @RequestParam String status) {
        RequirementStatus enumStatus = RequirementStatus.valueOf(status.toUpperCase());
        service.updateRfqStatus(requirementId, enumStatus);
        return "✅ Requirement " + requirementId + " updated to status " + enumStatus;
    }

}
