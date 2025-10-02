package com.zemicon.dto;

import java.time.LocalDateTime;

public class VendorQuote {
    private String vendorName;
    private Double unitPrice;
    private Integer leadTimeWeeks;
    private String dateCode;
    private Integer moq;
    private String remarks;
    private LocalDateTime submittedAt;
    
	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}
	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
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
	public String getDateCode() {
		return dateCode;
	}
	public void setDateCode(String dateCode) {
		this.dateCode = dateCode;
	}
	public Integer getMoq() {
		return moq;
	}
	public void setMoq(Integer moq) {
		this.moq = moq;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
