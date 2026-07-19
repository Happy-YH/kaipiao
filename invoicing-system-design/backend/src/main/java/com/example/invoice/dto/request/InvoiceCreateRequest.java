package com.example.invoice.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class InvoiceCreateRequest {
    private String invoiceType;
    private Long customerId;
    private Date issueDate;
    private String remark;
    private List<InvoiceDetailRequest> details;

    @Data
    public static class InvoiceDetailRequest {
        private Long contractId;
        private String contractNo;
        private Long repaymentRecordId;
        private String feeType;
        private Date interestStartDate;
        private Date interestEndDate;
        private BigDecimal principalAmount;
        private BigDecimal annualRate;
        private BigDecimal interestAmount;
        private BigDecimal taxRate;
        private BigDecimal totalAmount;
    }
}