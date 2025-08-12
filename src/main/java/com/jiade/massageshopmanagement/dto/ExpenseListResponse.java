package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.model.Expense;

import java.util.List;

public class ExpenseListResponse {
    private int totalRecords; // 总记录数
    private int totalPages;   // 总页数
    private int currentPage;  // 当前页码
    private List<Expense> records; // 费用记录列表

    public ExpenseListResponse(List<Expense> records, int totalRecords, int totalPages, int currentPage) {
        this.records = records;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public List<Expense> getRecords() {
        return records;
    }

    public void setRecords(List<Expense> records) {
        this.records = records;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
