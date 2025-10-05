package com.zemicon.dto;

import java.time.LocalDateTime;

public class VendorQuoteDto {
    public Long id;
    public String manufacturer;
    public String mpn;
    public Integer quantity;
    public Double unitPrice;
    public Integer leadTimeWeeks;
    public Integer moq;
    public String dateCode;
    public String remarks;
    public LocalDateTime submittedAt;

    // nested requirement (only subset)
    public static class Req {
        public Long id;
        public String mpn;
        public String manufacturer;
        public Integer quantity;
    }
    public Req requirement;
}
