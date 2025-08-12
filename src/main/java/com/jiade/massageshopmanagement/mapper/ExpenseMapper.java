package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.model.Expense;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ExpenseMapper {

    /**
     * 插入支出记录
     * @param expense 支出记录
     */
    void insertExpense(Expense expense);

    /**
     * 修改支出记录
     * @param id 支出记录ID
     * @param expense 更新后的支出记录
     */
    void modifyExpense(
            @Param("id") Long id,
            Expense expense
    );

    /**
     * 根据ID查询支出记录
     * @param id 支出记录ID
     * @return 支出记录
     */
    Expense selectById(@Param("id") Long id);

    /**
     * 统计支出记录数量
     * @param keyword 关键词（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param minAmount 最小金额（可选）
     * @param maxAmount 最大金额（可选）
     * @return 符合条件的支出记录数量
     */
    int countExpenses(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );

    /**
     * 统计已删除的支出记录数量
     * @param keyword 关键词（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param minAmount 最小金额（可选）
     * @param maxAmount 最大金额（可选）
     * @return 符合条件的已删除支出记录数量
     */
    int countDeletedExpenses(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );

    /**
     * 查询支出记录列表
     * @param keyword 关键词（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param minAmount 最小金额（可选）
     * @param maxAmount 最大金额（可选）
     * @param sortBy 排序字段（如 "spendDate" 或 "amount"）
     * @param order 排序方式（"ASC" 或 "DESC"）
     * @param offset 分页偏移量
     * @param size 每页大小
     * @return 符合条件的支出记录列表
     */
    List<Expense> selectExpenses(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("size") int size
    );

    List<Expense> selectDeletedExpenses(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 逻辑删除支出记录
     * @param id 支出记录ID
     */
    void logicDeleteExpense(@Param("id") Long id);

    /**
     * 恢复逻辑删除的支出记录
     * @param id 支出记录ID
     */
    void logicRestoreExpense(@Param("id") Long id);
}
