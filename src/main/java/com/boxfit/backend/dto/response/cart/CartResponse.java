package com.boxfit.backend.dto.response.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CartResponse {

    private UUID cartId;
    private List<CartItemResponse> items;
    private Integer totalItems;
    private BigDecimal cartTotal;

    public UUID getCartId() { return cartId; }
    public void setCartId(UUID cartId) { this.cartId = cartId; }

    public List<CartItemResponse> getItems() { return items; }
    public void setItems(List<CartItemResponse> items) { this.items = items; }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

    public BigDecimal getCartTotal() { return cartTotal; }
    public void setCartTotal(BigDecimal cartTotal) { this.cartTotal = cartTotal; }
}
