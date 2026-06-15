package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.address.AddressRequest;
import com.boxfit.backend.dto.response.address.AddressResponse;
import java.util.List;
import java.util.UUID;

public interface AddressService {
    List<AddressResponse> getMyAddresses();
    AddressResponse create(AddressRequest request);
    AddressResponse update(UUID addressId, AddressRequest request);
    void delete(UUID addressId);
}
