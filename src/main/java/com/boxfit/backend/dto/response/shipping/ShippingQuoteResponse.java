package com.boxfit.backend.dto.response.shipping;

import java.math.BigDecimal;
import java.util.List;

public class ShippingQuoteResponse {

    private String province;
    private String district;
    private String ward;
    private List<ShippingMethodResponse> methods;
    private BigDecimal lowestFee;

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public List<ShippingMethodResponse> getMethods() { return methods; }
    public void setMethods(List<ShippingMethodResponse> methods) { this.methods = methods; }

    public BigDecimal getLowestFee() { return lowestFee; }
    public void setLowestFee(BigDecimal lowestFee) { this.lowestFee = lowestFee; }
}
