package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.domain.entity.Address;
import com.boxfit.backend.domain.entity.User;
import com.boxfit.backend.dto.request.address.AddressRequest;
import com.boxfit.backend.dto.response.address.AddressResponse;
import com.boxfit.backend.repository.AddressRepository;
import com.boxfit.backend.service.AddressService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses() {
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(currentUser().getId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public AddressResponse create(AddressRequest request) {
        User user = currentUser();
        Address address = new Address();
        address.setUser(user);
        apply(request, address);
        return toResponse(addressRepository.save(address));
    }

    @Override
    public AddressResponse update(UUID addressId, AddressRequest request) {
        User user = currentUser();
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, "Address not found"));
        apply(request, address);
        return toResponse(addressRepository.save(address));
    }

    @Override
    public void delete(UUID addressId) {
        User user = currentUser();
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, "Address not found"));
        addressRepository.delete(address);
    }

    private User currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "Not authenticated");
        }
        return user;
    }

    private void apply(AddressRequest request, Address address) {
        address.setRecipientName(request.getRecipientName().trim());
        address.setPhoneNumber(request.getPhoneNumber().trim());
        address.setProvince(request.getProvince().trim());
        address.setDistrict(request.getDistrict().trim());
        address.setWard(request.getWard().trim());
        address.setStreetAddress(request.getStreetAddress().trim());
        address.setDefault(Boolean.TRUE.equals(request.getIsDefault()));
    }

    private AddressResponse toResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setRecipientName(address.getRecipientName());
        response.setPhoneNumber(address.getPhoneNumber());
        response.setProvince(address.getProvince());
        response.setDistrict(address.getDistrict());
        response.setWard(address.getWard());
        response.setStreetAddress(address.getStreetAddress());
        response.setFullAddress(toFullAddress(address));
        response.setDefault(address.isDefault());
        response.setCreatedAt(address.getCreatedAt());
        response.setUpdatedAt(address.getUpdatedAt());
        return response;
    }

    private String toFullAddress(Address address) {
        return String.join(", ",
            address.getStreetAddress(),
            address.getWard(),
            address.getDistrict(),
            address.getProvince()
        );
    }
}
