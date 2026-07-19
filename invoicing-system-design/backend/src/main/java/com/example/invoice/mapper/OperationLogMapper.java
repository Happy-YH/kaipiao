package com.example.invoice.mapper;

import com.example.invoice.entity.OperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OperationLogMapper {
    int insert(OperationLog log);
    List<OperationLog> selectByCondition(@Param("module") String module,
                                          @Param("operationType") String operationType,
                                          @Param("userId") Long userId,
                                          @Param("startDate") String startDate,
                                          @Param("endDate") String endDate,
                                          @Param("offset") int offset,
                                          @Param("size") int size);
    int countByCondition(@Param("module") String module,
                          @Param("operationType") String operationType,
                          @Param("userId") Long userId,
                          @Param("startDate") String startDate,
                          @Param("endDate") String endDate);
}
