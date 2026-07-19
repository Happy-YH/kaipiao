package com.example.invoice.controller;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.Customer;
import com.example.invoice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 客户信息控制器，提供客户信息的增删改查接口
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    /**
     * 查询客户列表，支持按名称模糊搜索
     * @param name 客户名称（可选）
     * @return 客户列表结果
     */
    @GetMapping
    public Result getAllCustomers(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return customerService.searchCustomersByName(name);
        }
        return customerService.getAllCustomers();
    }

    /**
     * 根据ID查询客户
     * @param id 客户ID
     * @return 客户详情结果
     */
    @GetMapping("/{id}")
    public Result getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    /**
     * 根据税号查询客户
     * @param taxId 纳税人识别号
     * @return 客户详情结果
     */
    @GetMapping("/tax/{taxId}")
    public Result getCustomerByTaxId(@PathVariable String taxId) {
        return customerService.getCustomerByTaxId(taxId);
    }

    /**
     * 创建客户
     * @param customer 客户信息
     * @return 创建结果
     */
    @PostMapping
    public Result createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    /**
     * 更新客户信息
     * @param id 客户ID
     * @param customer 客户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        return customerService.updateCustomer(customer);
    }

    /**
     * 删除客户
     * @param id 客户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result deleteCustomer(@PathVariable Long id) {
        return customerService.deleteCustomer(id);
    }
}
