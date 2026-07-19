package com.example.invoice.controller;

import com.example.invoice.dto.response.Result;
import com.example.invoice.kingdee.KingdeeInvoiceQueryResponse;
import com.example.invoice.kingdee.KingdeeResult;
import com.example.invoice.kingdee.KingdeeService;
import com.example.invoice.kingdee.KingdeeTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 金蝶对接控制器，提供金蝶API状态查询、发票查询、交付等接口
 */
@Slf4j
@RestController
@RequestMapping("/api/kingdee")
public class KingdeeController {

    @Autowired
    private KingdeeService kingdeeService;

    @Value("${kingdee.mock.enabled:true}")
    private boolean mockEnabled;

    /**
     * 获取金蝶API令牌
     * @return 令牌信息结果
     */
    @GetMapping("/token")
    public Result<KingdeeTokenResponse> getToken() {
        try {
            KingdeeTokenResponse token = kingdeeService.getToken();
            return Result.success(token);
        } catch (Exception e) {
            log.error("获取金蝶token失败", e);
            return Result.error("获取token失败：" + e.getMessage());
        }
    }

    /**
     * 根据发票编号查询金蝶发票信息
     * @param invoiceNo 发票编号
     * @return 发票查询结果
     */
    @GetMapping("/invoice/{invoiceNo}")
    public Result<KingdeeInvoiceQueryResponse> queryInvoice(@PathVariable String invoiceNo) {
        try {
            KingdeeInvoiceQueryResponse response = kingdeeService.queryInvoice(invoiceNo);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询发票失败", e);
            return Result.error("查询发票失败：" + e.getMessage());
        }
    }

    /**
     * 作废金蝶发票
     * @param invoiceNo 发票编号
     * @return 作废结果
     */
    @PostMapping("/invoice/{invoiceNo}/cancel")
    public Result cancelInvoice(@PathVariable String invoiceNo) {
        try {
            KingdeeResult result = kingdeeService.cancelInvoice(invoiceNo);
            if (result.isSuccess()) {
                return Result.success(result.getMessage());
            } else {
                return Result.error(result.getMessage());
            }
        } catch (Exception e) {
            log.error("作废发票失败", e);
            return Result.error("作废发票失败：" + e.getMessage());
        }
    }

    /**
     * 交付金蝶发票（发送邮件或短信）
     * @param invoiceNo 发票编号
     * @param email 接收邮箱（可选）
     * @param phone 接收手机号（可选）
     * @return 交付结果
     */
    @PostMapping("/invoice/{invoiceNo}/deliver")
    public Result deliverInvoice(@PathVariable String invoiceNo,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String phone) {
        try {
            KingdeeResult result = kingdeeService.deliverInvoice(invoiceNo, email, phone);
            if (result.isSuccess()) {
                return Result.success(result.getMessage(), result.getData());
            } else {
                return Result.error(result.getMessage());
            }
        } catch (Exception e) {
            log.error("交付发票失败", e);
            return Result.error("交付发票失败：" + e.getMessage());
        }
    }

    /**
     * 获取金蝶税目分类列表
     * @return 税目分类数据结果
     */
    @GetMapping("/tax-classifications")
    public Result<Map<String, Object>> getTaxClassificationList() {
        try {
            KingdeeResult<Map<String, Object>> result = kingdeeService.getTaxClassificationList();
            if (result.isSuccess()) {
                return Result.success(result.getData());
            } else {
                return Result.error(result.getMessage());
            }
        } catch (Exception e) {
            log.error("获取税目列表失败", e);
            return Result.error("获取税目列表失败：" + e.getMessage());
        }
    }

    /**
     * 查询金蝶API连接状态
     * @return 连接状态信息结果
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> getKingdeeStatus() {
        try {
            KingdeeTokenResponse token = kingdeeService.getToken();
            Map<String, Object> data = new HashMap<>();
            data.put("connected", true);
            data.put("mockEnabled", mockEnabled);
            data.put("tokenValid", token != null && token.getAccessToken() != null);
            return Result.success(data);
        } catch (Exception e) {
            Map<String, Object> data = new HashMap<>();
            data.put("connected", false);
            data.put("mockEnabled", mockEnabled);
            data.put("errorMessage", e.getMessage());
            return Result.success(data);
        }
    }
}
