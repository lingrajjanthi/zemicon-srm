package com.zemicon.dto;

public class VendorQuoteRequest {
    private String vendorName;
    private String customerName;
    private String manufacturer;
    private String mpn;
    private Integer quantity;
    private Double unitPrice;
    private Integer leadTimeWeeks;
    private Integer moq;
    private String dateCode;
    private String remarks;
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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
	public Double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public Integer getLeadTimeWeeks() {
		return leadTimeWeeks;
	}
	public void setLeadTimeWeeks(Integer leadTimeWeeks) {
		this.leadTimeWeeks = leadTimeWeeks;
	}
	public Integer getMoq() {
		return moq;
	}
	public void setMoq(Integer moq) {
		this.moq = moq;
	}
	public String getDateCode() {
		return dateCode;
	}
	public void setDateCode(String dateCode) {
		this.dateCode = dateCode;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}

