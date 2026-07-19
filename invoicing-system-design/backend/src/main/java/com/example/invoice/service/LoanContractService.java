package com.example.invoice.service;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.LoanContract;
import com.example.invoice.mapper.LoanContractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 贷款合同服务类，处理合同信息的存储与查询
 */
@Service
public class LoanContractService {
    @Autowired
    private LoanContractMapper loanContractMapper;

    /**
     * 查询所有合同列表
     *
     * @return 所有合同列表
     */
    public Result<List<LoanContract>> getAllContracts() {
        List<LoanContract> contracts = loanContractMapper.selectAll();
        return Result.success(contracts);
    }

    /**
     * 根据ID查询合同
     *
     * @param id 合同ID
     * @return 合同信息，不存在则返回错误
     */
    public Result<LoanContract> getContractById(Long id) {
        LoanContract contract = loanContractMapper.selectById(id);
        if (contract == null) {
            return Result.error("合同不存在");
        }
        return Result.success(contract);
    }

    public Result<LoanContract> getContractByNo(String contractNo) {
        LoanContract contract = loanContractMapper.selectByContractNo(contractNo);
        if (contract == null) {
            return Result.error("合同不存在");
        }
        return Result.success(contract);
    }

    /**
     * 根据客户ID查询关联的合同列表
     *
     * @param customerId 客户ID
     * @return 该客户关联的合同列表
     */
    public Result<List<LoanContract>> getContractsByCustomer(Long customerId) {
        List<LoanContract> contracts = loanContractMapper.selectByCustomerId(customerId);
        return Result.success(contracts);
    }

    public Result<LoanContract> createContract(LoanContract contract) {
        if (contract.getStatus() == null) {
            contract.setStatus("ACTIVE");
        }
        loanContractMapper.insert(contract);
        return Result.success("创建成功", contract);
    }

    /**
     * 更新合同信息
     *
     * @param contract 合同信息（含ID）
     * @return 更新成功的合同信息
     */
    public Result<LoanContract> updateContract(LoanContract contract) {
        LoanContract existing = loanContractMapper.selectById(contract.getId());
        if (existing == null) {
            return Result.error("合同不存在");
        }
        loanContractMapper.update(contract);
        return Result.success("更新成功", contract);
    }
}