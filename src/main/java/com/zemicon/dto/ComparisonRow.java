package com.zemicon.dto;

import java.util.Map;

public class ComparisonRow {
    private Long requirementId;
    private String manufacturer;
    private String mpn;
    private Integer quantity;
    private Double targetPrice;

    // vendorId → VendorQuote
    private Map<Long, VendorQuote> vendorQuotes;

    private VendorQuote l1Vendor;
    private VendorQuote l2Vendor;
	public Long getRequirementId() {
		return requirementId;
	}
	public void setRequirementId(Long requirementId) {
		this.requirementId = requirementId;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getMpn() {
		return mpn;
	}
	public void setMpn(String mpn) {
		this.mpn = mpn;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getTargetPrice() {
		return targetPrice;
	}
	public void setTargetPrice(Double targetPrice) {
		this.targetPrice = targetPrice;
	}
	public Map<Long, VendorQuote> getVendorQuotes() {
		return vendorQuotes;
	}
	public void setVendorQuotes(Map<Long, VendorQuote> vendorQuotes) {
		this.vendorQuotes = vendorQuotes;
	}
	public VendorQuote getL1Vendor() {
		return l1Vendor;
	}
	public void setL1Vendor(VendorQuote l1Vendor) {
		this.l1Vendor = l1Vendor;
	}
	public VendorQuote getL2Vendor() {
		return l2Vendor;
	}
	public void setL2Vendor(VendorQuote l2Vendor) {
		this.l2Vendor = l2Vendor;
	}

}
