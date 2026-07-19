package com.example.invoice.kingdee;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 金蝶令牌响应类，封装获取到的访问令牌信息
 */
@Data
public class KingdeeTokenResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 访问令牌 */
    private String accessToken;
    /** 令牌类型 */
    private String tokenType;
    /** 有效期秒数 */
    private Long expiresIn;
    /** 过期时间 */
    private Date expireTime;
    /** 刷新令牌 */
    private String refreshToken;
    /** 授权范围 */
    private String scope;
}
