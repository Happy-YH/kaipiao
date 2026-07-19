package com.example.invoice.mapper;

import com.example.invoice.entity.InvoiceDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface InvoiceDetailMapper {
    List<InvoiceDetail> selectByInvoiceId(@Param("invoiceId") Long invoiceId);
    int insert(InvoiceDetail detail);
    int deleteByInvoiceId(@Param("invoiceId") Long invoiceId);
    List<Map<String, Object>> countByFeeType();
}