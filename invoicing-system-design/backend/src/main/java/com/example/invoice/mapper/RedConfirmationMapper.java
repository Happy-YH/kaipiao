package com.example.invoice.mapper;

import com.example.invoice.entity.RedConfirmation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RedConfirmationMapper {
    List<RedConfirmation> selectAll();
    RedConfirmation selectById(@Param("id") Long id);
    RedConfirmation selectByConfirmationNo(@Param("confirmationNo") String confirmationNo);
    List<RedConfirmation> selectByInvoiceId(@Param("invoiceId") Long invoiceId);
    List<RedConfirmation> selectByStatus(@Param("status") String status);
    List<RedConfirmation> selectByCustomerId(@Param("customerId") Long customerId);
    int insert(RedConfirmation confirmation);
    int update(RedConfirmation confirmation);
}