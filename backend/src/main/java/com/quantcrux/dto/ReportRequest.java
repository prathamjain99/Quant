package com.quantcrux.dto;

import jakarta.validation.constraints.NotBlank;

public class ReportRequest {
    @NotBlank
    private String reportType;

    @NotBlank
    private String startDate;

    @NotBlank
    private String endDate;

    @NotBlank
    private String format;

    // Getters and Setters
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}