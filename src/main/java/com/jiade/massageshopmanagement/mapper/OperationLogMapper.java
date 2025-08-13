package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.model.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperationLogMapper {

    /**
     * 插入操作日志记录
     * @param log 操作日志记录
     */
    void insert(OperationLog log);

    /**
     * 根据ID查询操作日志记录
     * @param operation 操作类型
     * @param module 操作模块
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志记录
     */
    long countLogs(
            @Param("operation") String operation,
            @Param("module") String module,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 分页查询操作日志记录
     * @param operation 操作类型
     * @param module 操作模块
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param sortBy 排序字段
     * @param order 排序方式（asc或desc）
     * @param offset 分页偏移量
     * @param size 每页大小
     * @return 操作日志记录列表
     */
    List<OperationLog> selectLogs(
            @Param("operation") String operation,
            @Param("module") String module,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("size") int size
    );
}
