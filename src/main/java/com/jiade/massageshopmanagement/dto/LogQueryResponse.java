package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.model.OperationLog;

import java.util.List;

public class LogQueryResponse {
    private Long totalLogs;
    private int totalPages;
    private int currentPage;
    private List<OperationLog> logs;

    public LogQueryResponse(List<OperationLog> logs, Long totalLogs, int totalPages, int currentPage) {
        this.logs = logs;
        this.totalLogs = totalLogs;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public Long getTotalLogs() {
        return totalLogs;
    }
    public void setTotalLogs(Long totalLogs) {
        this.totalLogs = totalLogs;
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
    public List<OperationLog> getLogs() {
        return logs;
    }
    public void setLogs(List<OperationLog> logs) {
        this.logs = logs;
    }
}
