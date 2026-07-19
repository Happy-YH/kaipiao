package com.example.invoice.mapper;

import com.example.invoice.entity.LoanContract;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoanContractMapper {
    List<LoanContract> selectAll();
    LoanContract selectById(@Param("id") Long id);
    LoanContract selectByContractNo(@Param("contractNo") String contractNo);
    List<LoanContract> selectByCustomerId(@Param("customerId") Long customerId);
    int insert(LoanContract contract);
    int update(LoanContract contract);
}