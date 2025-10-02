package com.zemicon.controller;

import com.zemicon.dto.ComparisonResult;
import com.zemicon.service.VendorResponseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comparison")
public class ComparisonController {

    @Autowired
    private VendorResponseService service;

//    @GetMapping("/{customerId}")
//    public ComparisonResult compare(@PathVariable Long customerId) {
//        return service.buildComparisonForCustomer(customerId);
//    }
}
