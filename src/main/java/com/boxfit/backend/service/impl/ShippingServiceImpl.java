package com.boxfit.backend.service.impl;

import com.boxfit.backend.dto.request.shipping.ShippingQuoteRequest;
import com.boxfit.backend.dto.response.shipping.ShippingMethodResponse;
import com.boxfit.backend.dto.response.shipping.ShippingQuoteResponse;
import com.boxfit.backend.service.ShippingService;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Override
    public List<ShippingMethodResponse> getMethods() {
        return List.of(
            new ShippingMethodResponse("STANDARD", "Standard Shipping", BigDecimal.valueOf(30000), "3-5 days"),
            new ShippingMethodResponse("EXPRESS", "Express Shipping", BigDecimal.valueOf(50000), "1-2 days")
        );
    }

    @Override
    public ShippingQuoteResponse quote(ShippingQuoteRequest request) {
        List<ShippingMethodResponse> methods = getMethods();
        ShippingQuoteResponse response = new ShippingQuoteResponse();
        response.setProvince(request.getProvince().trim());
        response.setDistrict(request.getDistrict().trim());
        response.setWard(request.getWard().trim());
        response.setMethods(methods);
        response.setLowestFee(methods.stream()
            .map(ShippingMethodResponse::getFee)
            .min(Comparator.naturalOrder())
            .orElse(BigDecimal.ZERO));
        return response;
    }
}
