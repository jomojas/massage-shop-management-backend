package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.ExpenseDTO;
import com.jiade.massageshopmanagement.dto.ExpenseListResponse;
import com.jiade.massageshopmanagement.mapper.ExpenseMapper;
import com.jiade.massageshopmanagement.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseMapper expenseMapper;
    private static final Set<String> VALID_EXPENSE_CATEGORIES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                    "房租水电", "设备维修", "商店进货", "其他支出"
            )));

    public void addExpense(ExpenseDTO expenseDTO) {
        try {
            // 检查支出类型是否合法
            if (!VALID_EXPENSE_CATEGORIES.contains(expenseDTO.getCategory())) {
                throw new IllegalArgumentException("支出类型不合法");
            }
            if (expenseDTO.getAmount() == null || expenseDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("支出金额必须大于0");
            }
            Expense expense = new Expense();
            expense.setCategory(expenseDTO.getCategory());
            expense.setAmount(expenseDTO.getAmount());
            expense.setSpendDate(expenseDTO.getSpendDate());
            expense.setDescription(expenseDTO.getDescription());
            expenseMapper.insertExpense(expense);
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("添加支出失败: " + e.getMessage(), e);
        }
    }

    public void modifyExpense (Long id, ExpenseDTO expenseDTO) {
        try {
            // 检查id是否存在且未被删除
            Expense existExpense = expenseMapper.selectById(id);
            if (existExpense == null || existExpense.getIsDeleted() != 0) {
                throw new IllegalArgumentException("支出记录不存在");
            }
            // 检查支出类型是否合法
            if (!VALID_EXPENSE_CATEGORIES.contains(expenseDTO.getCategory())) {
                throw new IllegalArgumentException("支出类型不合法");
            }
            if (expenseDTO.getAmount() == null || expenseDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("支出金额必须大于0");
            }
            Expense expense = new Expense();
            expense.setCategory(expenseDTO.getCategory());
            expense.setAmount(expenseDTO.getAmount());
            expense.setSpendDate(expenseDTO.getSpendDate());
            expense.setDescription(expenseDTO.getDescription());
            expenseMapper.modifyExpense(id, expense);
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("添加支出失败: " + e.getMessage(), e);
        }
    }

    public ExpenseListResponse getExpenses(
            String keyword, String category, LocalDate startDate, LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount, String sortBy, String order, int page, int size
            ) {
        try {
            // 检查支出类型是否合法
            if (category != null && !category.isEmpty() && !VALID_EXPENSE_CATEGORIES.contains(category)) {
                throw new IllegalArgumentException("支出类型不合法");
            }
            int offset = (page - 1) * size;
            // 1. 查询总数
            int totalRecords = expenseMapper.countExpenses(
                    keyword, category, startDate, endDate, minAmount, maxAmount
            );;
            // 2. 计算总页数
            int totalPages = (int) Math.ceil(totalRecords * 1.0 / size);
            // 3. 查询分页数据
            List<Expense> records = expenseMapper.selectExpenses(
                    keyword, category, startDate, endDate, minAmount, maxAmount, sortBy, order, offset, size
            );

            return new ExpenseListResponse(records, totalRecords, totalPages, page); // 返回分页结果
        } catch (Exception e) {
            throw new RuntimeException("获取支出记录失败: " + e.getMessage(), e);
        }
    }

    public ExpenseListResponse getDeletedExpenses(
            String keyword, String category, LocalDate startDate, LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount, String sortBy, String order, int page, int size
    ) {
        try {
            // 检查支出类型是否合法
            if (category != null && !category.isEmpty() && !VALID_EXPENSE_CATEGORIES.contains(category)) {
                throw new IllegalArgumentException("支出类型不合法");
            }
            int offset = (page - 1) * size;
            // 1. 查询总数
            int totalRecords = expenseMapper.countDeletedExpenses(
                    keyword, category, startDate, endDate, minAmount, maxAmount
            );;
            // 2. 计算总页数
            int totalPages = (int) Math.ceil(totalRecords * 1.0 / size);
            // 3. 查询分页数据
            List<Expense> records = expenseMapper.selectDeletedExpenses(
                    keyword, category, startDate, endDate, minAmount, maxAmount, sortBy, order, offset, size
            );

            return new ExpenseListResponse(records, totalRecords, totalPages, page); // 返回分页结果
        } catch (Exception e) {
            throw new RuntimeException("获取支出记录失败: " + e.getMessage(), e);
        }
    }

    public void deleteExpense(Long id) {
        try {
            Expense expense = expenseMapper.selectById(id);
            if (expense == null || expense.getIsDeleted() != 0) {
                throw new IllegalArgumentException("支出记录不存在或已删除");
            }
            expenseMapper.logicDeleteExpense(id);
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("删除支出记录失败: " + e.getMessage(), e);
        }
    }

    public void restoreExpense(Long id) {
        try {
            Expense expense = expenseMapper.selectById(id);
            if (expense == null || expense.getIsDeleted() != 1) {
                throw new IllegalArgumentException("支出记录不存在或已被恢复");
            }
            expenseMapper.logicRestoreExpense(id);
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数异常
        } catch (Exception e) {
            throw new RuntimeException("恢复支出记录失败: " + e.getMessage(), e);
        }
    }

    public List<String> getExpenseCategories() {
        return new ArrayList<>(VALID_EXPENSE_CATEGORIES);
    }
}
