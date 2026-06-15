package com.boxfit.backend.dto.response.shipping;

import java.math.BigDecimal;

public class ShippingMethodResponse {

    private String code;
    private String name;
    private BigDecimal fee;
    private String estimatedDelivery;

    public ShippingMethodResponse(String code, String name, BigDecimal fee, String estimatedDelivery) {
        this.code = code;
        this.name = name;
        this.fee = fee;
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
}
