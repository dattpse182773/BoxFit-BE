package com.boxfit.backend.controller;

import com.boxfit.backend.common.response.ApiResponse;
import com.boxfit.backend.dto.request.address.AddressRequest;
import com.boxfit.backend.dto.response.address.AddressResponse;
import com.boxfit.backend.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/addresses")
@Tag(name = "Address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách địa chỉ của tôi")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", addressService.getMyAddresses()));
    }

    @PostMapping
    @Operation(summary = "Thêm địa chỉ giao hàng")
    public ResponseEntity<ApiResponse<AddressResponse>> create(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", addressService.create(request)));
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Cập nhật địa chỉ giao hàng")
    public ResponseEntity<ApiResponse<AddressResponse>> update(@PathVariable UUID addressId,
                                                               @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", addressService.update(addressId, request)));
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Xóa địa chỉ giao hàng")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID addressId) {
        addressService.delete(addressId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", null));
    }
}
