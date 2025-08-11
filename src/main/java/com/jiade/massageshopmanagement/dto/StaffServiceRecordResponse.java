package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.dto.StaffServiceRecordDTO;
import java.util.List;

public class StaffServiceRecordResponse {
    private int totalRecords;
    private int totalPages;
    private int currentPage;
    private List<StaffServiceRecordDTO> records;

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
    public List<StaffServiceRecordDTO> getRecords() {
        return records;
    }
    public void setRecords(List<StaffServiceRecordDTO> records) {
        this.records = records;
    }
}
