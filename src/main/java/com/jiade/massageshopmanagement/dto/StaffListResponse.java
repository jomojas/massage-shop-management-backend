package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.model.Staff;

import java.util.List;

public class StaffListResponse {
    private int totalEmployees;
    private int totalPages;
    private int currentPage;
    private List<Staff> employees;

    public int getTotalEmployees() {
        return totalEmployees;
    }
    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
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
    public List<Staff> getEmployees() {
        return employees;
    }
    public void setEmployees(List<Staff> employees) {
        this.employees = employees;
    }
}
