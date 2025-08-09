package com.jiade.massageshopmanagement.dto;

import java.util.List;
import com.jiade.massageshopmanagement.model.RechargeRecord;

public class RechargeRecordResponse {
    private List<RechargeRecord> records;
    private int totalRecords;
    private int totalPages;
    private int currentPage;

    public RechargeRecordResponse(List<RechargeRecord> records, int totalRecords, int totalPages, int currentPage) {
        this.records = records;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public List<RechargeRecord> getList() {
        return records;
    }

    public void setList(List<RechargeRecord> records) {
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
