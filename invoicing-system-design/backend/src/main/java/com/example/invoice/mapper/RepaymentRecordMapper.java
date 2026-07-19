package com.example.invoice.mapper;

import com.example.invoice.entity.RepaymentRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RepaymentRecordMapper {
    List<RepaymentRecord> selectAll();
    RepaymentRecord selectById(@Param("id") Long id);
    RepaymentRecord selectByRecordNo(@Param("recordNo") String recordNo);
    List<RepaymentRecord> selectByContractId(@Param("contractId") Long contractId);
    List<RepaymentRecord> selectByInvoiceStatus(@Param("invoiceStatus") String invoiceStatus);
    List<RepaymentRecord> selectByCustomerId(@Param("customerId") Long customerId);
    int insert(RepaymentRecord record);
    int update(RepaymentRecord record);
}