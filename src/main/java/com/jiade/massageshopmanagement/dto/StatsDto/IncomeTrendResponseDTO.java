package com.jiade.massageshopmanagement.dto.StatsDto;

import java.util.List;

public class IncomeTrendResponseDTO {
    private String period;
    private String dimension;
    private List<IncomeTrendValueDTO> values;

    public IncomeTrendResponseDTO() {}
    public IncomeTrendResponseDTO(String period, String dimension, List<IncomeTrendValueDTO> values) {
        this.period = period;
        this.dimension = dimension;
        this.values = values;
    }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public List<IncomeTrendValueDTO> getValues() { return values; }
    public void setValues(List<IncomeTrendValueDTO> values) { this.values = values; }
}
