package com.zemicon.dto;

public class FinalQuoteItem {
    private Long requirementId;
    private double finalQuote;

    // Getters & Setters
    public Long getRequirementId() { return requirementId; }
    public void setRequirementId(Long requirementId) { this.requirementId = requirementId; }

    public double getFinalQuote() { return finalQuote; }
    public void setFinalQuote(double finalQuote) { this.finalQuote = finalQuote; }
}
