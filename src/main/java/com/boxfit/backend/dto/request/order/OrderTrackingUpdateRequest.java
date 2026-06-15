package com.boxfit.backend.dto.request.order;

import jakarta.validation.constraints.NotBlank;

public class OrderTrackingUpdateRequest {

    @NotBlank
    private String trackingCode;

    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
}
