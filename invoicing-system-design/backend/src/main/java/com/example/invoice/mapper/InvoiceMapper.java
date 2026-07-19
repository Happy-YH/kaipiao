package com.example.invoice.mapper;

import com.example.invoice.entity.Invoice;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface InvoiceMapper {
    List<Invoice> selectAll();
    Invoice selectById(@Param("id") Long id);
    Invoice selectByInvoiceNo(@Param("invoiceNo") String invoiceNo);
    List<Invoice> selectByCustomerId(@Param("customerId") Long customerId);
    List<Invoice> selectByStatus(@Param("status") String status);
    List<Invoice> selectByInvoiceKind(@Param("invoiceKind") String invoiceKind);
    List<Invoice> selectByOriginalInvoiceId(@Param("originalInvoiceId") Long originalInvoiceId);
    int insert(Invoice invoice);
    int update(Invoice invoice);
    int countByMonthAndKind(@Param("month") String month, @Param("kind") String kind);
    BigDecimal sumAmountByMonthAndKind(@Param("month") String month, @Param("kind") String kind);
    int countAll();
    BigDecimal sumAmountByKind(@Param("kind") String kind);
    int countByStatus(@Param("status") String status);

    List<Map<String, Object>> selectCustomerSummary(@Param("limit") int limit);

    List<Invoice> selectByCondition(@Param("status") String status,
                                    @Param("startDate") String startDate,
                                    @Param("endDate") String endDate,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit,
                                    @Param("contractNo") String contractNo,
                                    @Param("invoiceType") String invoiceType);
}