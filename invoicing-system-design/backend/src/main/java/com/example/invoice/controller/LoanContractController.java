package com.example.invoice.controller;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.LoanContract;
import com.example.invoice.service.LoanContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 贷款合同控制器，提供合同信息的增删改查接口
 */
@RestController
@RequestMapping("/api/contracts")
public class LoanContractController {
    @Autowired
    private LoanContractService loanContractService;

    /**
     * 查询合同列表，支持按客户ID筛选
     * @param customerId 客户ID（可选）
     * @return 合同列表结果
     */
    @GetMapping
    public Result getAllContracts(@RequestParam(required = false) Long customerId) {
        if (customerId != null) {
            return loanContractService.getContractsByCustomer(customerId);
        }
        return loanContractService.getAllContracts();
    }

    /**
     * 根据ID查询合同
     * @param id 合同ID
     * @return 合同详情结果
     */
    @GetMapping("/{id}")
    public Result getContractById(@PathVariable Long id) {
        return loanContractService.getContractById(id);
    }

    /**
     * 根据合同编号查询合同
     * @param contractNo 合同编号
     * @return 合同详情结果
     */
    @GetMapping("/no/{contractNo}")
    public Result getContractByNo(@PathVariable String contractNo) {
        return loanContractService.getContractByNo(contractNo);
    }

    /**
     * 创建合同
     * @param contract 合同信息
     * @return 创建结果
     */
    @PostMapping
    public Result createContract(@RequestBody LoanContract contract) {
        return loanContractService.createContract(contract);
    }

    /**
     * 更新合同信息
     * @param id 合同ID
     * @param contract 合同信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result updateContract(@PathVariable Long id, @RequestBody LoanContract contract) {
        contract.setId(id);
        return loanContractService.updateContract(contract);
    }
}
