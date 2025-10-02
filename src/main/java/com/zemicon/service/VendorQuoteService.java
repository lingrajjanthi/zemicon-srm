package com.zemicon.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zemicon.dto.VendorQuote;
import com.zemicon.model.VendorResponse;
import com.zemicon.repo.VendorResponseRepository;

@Service
public class VendorQuoteService {

    @Autowired
    private VendorResponseRepository responseRepo;

    public Map<String, List<VendorQuote>> getCustomerQuotes(Long customerId) {
        List<VendorResponse> responses = responseRepo.findByRequirement_Customer_IdOrderByRequirement_MpnAscSubmittedAtDesc(customerId);

        Map<String, List<VendorQuote>> productMap = new LinkedHashMap<>();

        for (VendorResponse vr : responses) {
            String mpn = vr.getRequirement().getMpn();

            VendorQuote quote = new VendorQuote();
            quote.setVendorName(vr.getVendor().getName());
            quote.setUnitPrice(vr.getUnitPrice() != null ? vr.getUnitPrice() : null);
            quote.setLeadTimeWeeks(vr.getLeadTimeWeeks());
            quote.setDateCode(vr.getDateCode());
            quote.setMoq(vr.getMoq());
            quote.setRemarks(vr.getRemarks());
            quote.setSubmittedAt(vr.getSubmittedAt());

            productMap.computeIfAbsent(mpn, k -> new ArrayList<>()).add(quote);
        }

        return productMap;
    }
}
