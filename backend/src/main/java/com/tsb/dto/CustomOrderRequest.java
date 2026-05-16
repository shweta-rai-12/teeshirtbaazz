package com.tsb.dto;

public class CustomOrderRequest {
    private String desiredSize;
    private String desiredColor;
    private String logoUrl;
    private String notes;
    private Double estimatedPrice;

    public CustomOrderRequest() {
    }

    public String getDesiredSize() {
        return desiredSize;
    }

    public void setDesiredSize(String desiredSize) {
        this.desiredSize = desiredSize;
    }

    public String getDesiredColor() {
        return desiredColor;
    }

    public void setDesiredColor(String desiredColor) {
        this.desiredColor = desiredColor;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
}
