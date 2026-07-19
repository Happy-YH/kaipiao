package com.example.invoice.mapper;

import com.example.invoice.entity.Customer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerMapper {
    List<Customer> selectAll();
    Customer selectById(@Param("id") Long id);
    Customer selectByTaxId(@Param("taxId") String taxId);
    List<Customer> selectByName(@Param("customerName") String customerName);
    int insert(Customer customer);
    int update(Customer customer);
    int deleteById(@Param("id") Long id);
}