package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.shipping.ShippingQuoteRequest;
import com.boxfit.backend.dto.response.shipping.ShippingMethodResponse;
import com.boxfit.backend.dto.response.shipping.ShippingQuoteResponse;
import java.util.List;

public interface ShippingService {
    List<ShippingMethodResponse> getMethods();
    ShippingQuoteResponse quote(ShippingQuoteRequest request);
}
