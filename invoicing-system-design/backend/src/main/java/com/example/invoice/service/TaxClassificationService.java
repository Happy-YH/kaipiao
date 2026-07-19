package com.example.invoice.service;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.TaxClassification;
import com.example.invoice.mapper.TaxClassificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 税目分类服务类，管理税目编码和税率配置
 */
@Service
public class TaxClassificationService {
    @Autowired
    private TaxClassificationMapper taxClassificationMapper;

    /**
     * 查询所有税目分类列表
     *
     * @return 所有税目分类列表
     */
    public Result<List<TaxClassification>> getAllTaxClassifications() {
        List<TaxClassification> classifications = taxClassificationMapper.selectAll();
        return Result.success(classifications);
    }

    /**
     * 根据ID查询税目分类
     *
     * @param id 税目分类ID
     * @return 税目分类信息，不存在则返回错误
     */
    public Result<TaxClassification> getTaxClassificationById(Long id) {
        TaxClassification classification = taxClassificationMapper.selectById(id);
        if (classification == null) {
            return Result.error("税收分类不存在");
        }
        return Result.success(classification);
    }

    /**
     * 根据税目编码查询税目分类
     *
     * @param taxCode 税目编码
     * @return 税目分类信息，不存在则返回错误
     */
    public Result<TaxClassification> getTaxClassificationByCode(String taxCode) {
        TaxClassification classification = taxClassificationMapper.selectByTaxCode(taxCode);
        if (classification == null) {
            return Result.error("税收分类不存在");
        }
        return Result.success(classification);
    }

    /**
     * 根据费用类型查询税目分类
     *
     * @param feeType 费用类型
     * @return 税目分类信息，不存在则返回错误
     */
    public Result<TaxClassification> getTaxClassificationByFeeType(String feeType) {
        TaxClassification classification = taxClassificationMapper.selectByFeeType(feeType);
        if (classification == null) {
            return Result.error("税收分类不存在");
        }
        return Result.success(classification);
    }

    /**
     * 创建税目分类，默认启用状态
     *
     * @param classification 税目分类信息
     * @return 创建成功的税目分类
     */
    public Result<TaxClassification> createTaxClassification(TaxClassification classification) {
        classification.setStatus(1);
        taxClassificationMapper.insert(classification);
        return Result.success("创建成功", classification);
    }

    /**
     * 更新税目分类信息
     *
     * @param classification 税目分类信息（含ID）
     * @return 更新成功的税目分类
     */
    public Result<TaxClassification> updateTaxClassification(TaxClassification classification) {
        TaxClassification existing = taxClassificationMapper.selectById(classification.getId());
        if (existing == null) {
            return Result.error("税收分类不存在");
        }
        taxClassificationMapper.update(classification);
        return Result.success("更新成功", classification);
    }
}