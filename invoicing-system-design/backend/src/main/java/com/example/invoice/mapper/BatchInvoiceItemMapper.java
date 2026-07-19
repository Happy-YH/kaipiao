package com.example.invoice.mapper;

import com.example.invoice.entity.BatchInvoiceItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BatchInvoiceItemMapper {
    List<BatchInvoiceItem> selectByTaskId(@Param("taskId") Long taskId);
    int insert(BatchInvoiceItem item);
    int update(BatchInvoiceItem item);
    int deleteByTaskId(@Param("taskId") Long taskId);
}