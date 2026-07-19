package com.example.invoice.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RedCancelRequest {
    private Long invoiceId;
    private String cancelType;
    private String cancelReason;
    private BigDecimal cancelAmount;
    private String remark;
    private String redRepaymentRecordIds;
    private String redReasonCode;
}