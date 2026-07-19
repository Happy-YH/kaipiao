package com.example.invoice.controller;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.TaxClassification;
import com.example.invoice.service.TaxClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 税目分类控制器，提供税目编码和税率的增删改查接口
 */
@RestController
@RequestMapping("/api/tax-classifications")
public class TaxClassificationController {
    @Autowired
    private TaxClassificationService taxClassificationService;

    /**
     * 查询税目分类列表，支持按费用类型筛选
     * @param feeType 费用类型（可选）
     * @return 税目分类列表结果
     */
    @GetMapping
    public Result getAllTaxClassifications(@RequestParam(required = false) String feeType) {
        if (feeType != null && !feeType.isEmpty()) {
            return taxClassificationService.getTaxClassificationByFeeType(feeType);
        }
        return taxClassificationService.getAllTaxClassifications();
    }

    /**
     * 根据ID查询税目分类
     * @param id 税目分类ID
     * @return 税目分类详情结果
     */
    @GetMapping("/{id}")
    public Result getTaxClassificationById(@PathVariable Long id) {
        return taxClassificationService.getTaxClassificationById(id);
    }

    /**
     * 根据税目编码查询税目分类
     * @param taxCode 税目编码
     * @return 税目分类详情结果
     */
    @GetMapping("/code/{taxCode}")
    public Result getTaxClassificationByCode(@PathVariable String taxCode) {
        return taxClassificationService.getTaxClassificationByCode(taxCode);
    }

    /**
     * 创建税目分类
     * @param classification 税目分类信息
     * @return 创建结果
     */
    @PostMapping
    public Result createTaxClassification(@RequestBody TaxClassification classification) {
        return taxClassificationService.createTaxClassification(classification);
    }

    /**
     * 更新税目分类
     * @param id 税目分类ID
     * @param classification 税目分类信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result updateTaxClassification(@PathVariable Long id, @RequestBody TaxClassification classification) {
        classification.setId(id);
        return taxClassificationService.updateTaxClassification(classification);
    }
}
