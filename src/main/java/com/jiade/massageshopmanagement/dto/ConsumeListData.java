package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.dto.ConsumptionRecordDetail;
import java.util.List;

public class ConsumeListData {
    private int totalConsumes;
    private int totalPages;
    private int currentPage;
    private List<ConsumptionRecordDetail> consumptions;

    public ConsumeListData(List<ConsumptionRecordDetail> consumptions, int totalConsumes, int totalPages, int currentPage) {
        this.totalConsumes = totalConsumes;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.consumptions = consumptions;
    }

    public int getTotalConsumes() {
        return totalConsumes;
    }

    public void setTotalConsumes(int totalConsumes) {
        this.totalConsumes = totalConsumes;
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

    public List<ConsumptionRecordDetail> getConsumptions() {
        return consumptions;
    }
    public void setConsumptions(List<ConsumptionRecordDetail> consumptions) {
        this.consumptions = consumptions;
    }
}
