package com.example.invoice.mapper;

import com.example.invoice.entity.BatchInvoiceTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BatchInvoiceTaskMapper {
    List<BatchInvoiceTask> selectAll();
    BatchInvoiceTask selectById(@Param("id") Long id);
    BatchInvoiceTask selectByTaskNo(@Param("taskNo") String taskNo);
    List<BatchInvoiceTask> selectByStatus(@Param("status") String status);
    int insert(BatchInvoiceTask task);
    int update(BatchInvoiceTask task);
}