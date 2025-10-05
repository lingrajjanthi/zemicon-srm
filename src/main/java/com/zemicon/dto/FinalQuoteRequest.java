package com.zemicon.dto;

import java.util.List;

public class FinalQuoteRequest {
    private Long customerId;
    private String email;
    private List<FinalQuoteItem> items;

    // Getters & Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<FinalQuoteItem> getItems() { return items; }
    public void setItems(List<FinalQuoteItem> items) { this.items = items; }
}
