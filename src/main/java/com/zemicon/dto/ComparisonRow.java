package com.zemicon.dto;

import java.util.Map;

import java.util.List;

public class ComparisonRow {
    private Long requirementId;
    private String manufacturer;
    private String mpn;
    private Integer quantity;
    private Double targetPrice;

    private List<VendorQuote> vendorQuotes; // sorted by lowest bid first
    private String bestVendor; // convenience field

    // Getters & Setters
    public Long getRequirementId() { return requirementId; }
    public void setRequirementId(Long requirementId) { this.requirementId = requirementId; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getMpn() { return mpn; }
    public void setMpn(String mpn) { this.mpn = mpn; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(Double targetPrice) { this.targetPrice = targetPrice; }
    public List<VendorQuote> getVendorQuotes() { return vendorQuotes; }
    public void setVendorQuotes(List<VendorQuote> vendorQuotes) { this.vendorQuotes = vendorQuotes; }
    public String getBestVendor() { return bestVendor; }
    public void setBestVendor(String bestVendor) { this.bestVendor = bestVendor; }
}

