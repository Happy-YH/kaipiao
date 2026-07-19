package com.example.invoice.service;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.Customer;
import com.example.invoice.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户信息服务类，处理客户信息的增删改查
 */
@Service
public class CustomerService {
    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 查询所有客户列表
     *
     * @return 所有客户列表
     */
    public Result<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerMapper.selectAll();
        return Result.success(customers);
    }

    /**
     * 根据ID查询客户
     *
     * @param id 客户ID
     * @return 客户信息，不存在则返回错误
     */
    public Result<Customer> getCustomerById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        return Result.success(customer);
    }

    /**
     * 根据税号查询客户
     *
     * @param taxId 税号
     * @return 客户信息，不存在则返回错误
     */
    public Result<Customer> getCustomerByTaxId(String taxId) {
        Customer customer = customerMapper.selectByTaxId(taxId);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        return Result.success(customer);
    }

    /**
     * 按名称模糊查询客户列表
     *
     * @param name 客户名称关键字
     * @return 匹配的客户列表
     */
    public Result<List<Customer>> searchCustomersByName(String name) {
        List<Customer> customers = customerMapper.selectByName(name);
        return Result.success(customers);
    }

    /**
     * 创建客户，默认启用状态
     *
     * @param customer 客户信息
     * @return 创建成功的客户信息
     */
    public Result<Customer> createCustomer(Customer customer) {
        customer.setStatus(1);
        customerMapper.insert(customer);
        return Result.success("创建成功", customer);
    }

    public Result<Customer> updateCustomer(Customer customer) {
        Customer existing = customerMapper.selectById(customer.getId());
        if (existing == null) {
            return Result.error("客户不存在");
        }
        customerMapper.update(customer);
        return Result.success("更新成功", customer);
    }

    /**
     * 删除客户
     *
     * @param id 客户ID
     * @return 删除结果
     */
    public Result deleteCustomer(Long id) {
        Customer existing = customerMapper.selectById(id);
        if (existing == null) {
            return Result.error("客户不存在");
        }
        customerMapper.deleteById(id);
        return Result.success("删除成功");
    }
}