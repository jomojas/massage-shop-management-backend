package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.model.Project;

import java.util.List;

public class ProjectListResponse {
    private int totalProjects;
    private int currentPage;
    private int totalPages;
    private List<Project> projects;

    public ProjectListResponse(List<Project> projects, int totalProjects, int totalPages, int currentPage) {
        this.totalProjects = totalProjects;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.projects = projects;
    }

    public int getTotalProjects() {
        return totalProjects;
    }
    public void setTotalProjects(int totalProjects) {
        this.totalProjects = totalProjects;
    }

    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Project> getProjects() {
        return projects;
    }
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
