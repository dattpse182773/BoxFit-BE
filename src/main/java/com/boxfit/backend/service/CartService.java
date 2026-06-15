package com.boxfit.backend.service;

import com.boxfit.backend.dto.request.cart.AddCartItemRequest;
import com.boxfit.backend.dto.request.cart.UpdateCartItemRequest;
import com.boxfit.backend.dto.response.cart.CartResponse;
import java.util.UUID;

public interface CartService {
    CartResponse getMyCart();
    CartResponse addItem(AddCartItemRequest request);
    CartResponse updateItem(UUID cartItemId, UpdateCartItemRequest request);
    void removeItem(UUID cartItemId);
    void clearCart();
}
