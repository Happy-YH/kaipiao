package com.example.invoice.mapper;

import com.example.invoice.entity.TaxClassification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaxClassificationMapper {
    List<TaxClassification> selectAll();
    TaxClassification selectById(@Param("id") Long id);
    TaxClassification selectByTaxCode(@Param("taxCode") String taxCode);
    TaxClassification selectByFeeType(@Param("feeType") String feeType);
    int insert(TaxClassification classification);
    int update(TaxClassification classification);
}