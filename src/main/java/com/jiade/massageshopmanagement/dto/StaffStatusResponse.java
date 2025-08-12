package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.dto.StaffStatusRecord;

import java.util.List;

public class StaffStatusResponse {
    private List<StaffStatusRecord> records;
    private int totalRecords;
    private int totalPages;
    private int currentPage;

    public StaffStatusResponse(List<StaffStatusRecord> records, int totalRecords, int totalPages, int currentPage) {
        this.records = records;
        this.totalRecords = totalRecords;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public List<StaffStatusRecord> getRecords() {
        return records;
    }
    public void setRecords(List<StaffStatusRecord> records) {
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
