package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.domain.entity.Cart;
import com.boxfit.backend.domain.entity.CartItem;
import com.boxfit.backend.domain.entity.Product;
import com.boxfit.backend.domain.entity.ProductVariant;
import com.boxfit.backend.domain.entity.Role;
import com.boxfit.backend.domain.entity.User;
import com.boxfit.backend.dto.request.cart.AddCartItemRequest;
import com.boxfit.backend.dto.request.cart.UpdateCartItemRequest;
import com.boxfit.backend.dto.response.cart.CartItemResponse;
import com.boxfit.backend.dto.response.cart.CartResponse;
import com.boxfit.backend.repository.CartItemRepository;
import com.boxfit.backend.repository.CartRepository;
import com.boxfit.backend.repository.ProductVariantRepository;
import com.boxfit.backend.service.CartService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductVariantRepository productVariantRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public CartResponse getMyCart() {
        return toResponse(getOrCreateCart(currentCustomer()));
    }

    @Override
    public CartResponse addItem(AddCartItemRequest request) {
        User user = currentCustomer();
        int quantity = validQuantity(request.getQuantity());
        ProductVariant variant = loadVariant(request.getVariantId());
        validateVariantAvailable(variant);

        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId())
            .orElseGet(() -> {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setVariant(variant);
                newItem.setQuantity(0);
                cart.getItems().add(newItem);
                return newItem;
            });

        int newQuantity = item.getQuantity() + quantity;
        validateQuantityWithinStock(newQuantity, variant);
        item.setQuantity(newQuantity);
        cartItemRepository.save(item);
        return toResponse(cartRepository.findByUserId(user.getId()).orElse(cart));
    }

    @Override
    public CartResponse updateItem(UUID cartItemId, UpdateCartItemRequest request) {
        User user = currentCustomer();
        int quantity = validQuantity(request.getQuantity());
        CartItem item = cartItemRepository.findByIdAndCartUserId(cartItemId, user.getId())
            .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND, "Cart item not found"));

        ProductVariant variant = item.getVariant();
        validateVariantAvailable(variant);
        validateQuantityWithinStock(quantity, variant);
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return toResponse(cartRepository.findByUserId(user.getId()).orElseThrow());
    }

    @Override
    public void removeItem(UUID cartItemId) {
        User user = currentCustomer();
        CartItem item = cartItemRepository.findByIdAndCartUserId(cartItemId, user.getId())
            .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND, "Cart item not found"));
        cartItemRepository.delete(item);
    }

    @Override
    public void clearCart() {
        User user = currentCustomer();
        cartRepository.findByUserId(user.getId())
            .ifPresent(cart -> cartItemRepository.deleteByCartId(cart.getId()));
    }

    private User currentCustomer() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AppException(ErrorCode.CART_ACCESS_DENIED, "Cart access denied");
        }
        if (user.getRole() == null || !Role.CUSTOMER.equals(user.getRole().getName())) {
            throw new AppException(ErrorCode.CART_ACCESS_DENIED, "Cart access denied");
        }
        return user;
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private ProductVariant loadVariant(UUID variantId) {
        return productVariantRepository.findById(variantId)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND, "Product variant not found"));
    }

    private int validQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new AppException(ErrorCode.INVALID_QUANTITY, "Quantity must be greater than 0");
        }
        return quantity;
    }

    private void validateVariantAvailable(ProductVariant variant) {
        if (!variant.isActive()) {
            throw new AppException(ErrorCode.PRODUCT_VARIANT_INACTIVE, "Product variant is inactive");
        }
        if (variant.getStockQuantity() <= 0) {
            throw new AppException(ErrorCode.OUT_OF_STOCK, "Product variant is out of stock");
        }
    }

    private void validateQuantityWithinStock(int quantity, ProductVariant variant) {
        if (quantity > variant.getStockQuantity()) {
            throw new AppException(ErrorCode.QUANTITY_EXCEEDS_STOCK, "Quantity exceeds stock");
        }
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
            .map(this::toItemResponse)
            .toList();

        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setItems(items);
        response.setTotalItems(items.stream().mapToInt(CartItemResponse::getQuantity).sum());
        response.setCartTotal(items.stream()
            .map(CartItemResponse::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        return response;
    }

    private CartItemResponse toItemResponse(CartItem item) {
        ProductVariant variant = item.getVariant();
        Product product = variant.getProduct();
        BigDecimal unitPrice = calculateUnitPrice(product, variant);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        CartItemResponse response = new CartItemResponse();
        response.setCartItemId(item.getId());
        response.setVariantId(variant.getId());
        response.setProductName(product.getName());
        response.setImageUrl(product.getImageUrl());
        response.setSku(variant.getSku());
        response.setSizeName(variant.getSize() == null ? null : variant.getSize().name());
        response.setColorName(variant.getColor());
        response.setUnitPrice(unitPrice);
        response.setQuantity(item.getQuantity());
        response.setTotalPrice(totalPrice);
        response.setStockQuantity(variant.getStockQuantity());
        return response;
    }

    private BigDecimal calculateUnitPrice(Product product, ProductVariant variant) {
        BigDecimal basePrice = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
        BigDecimal adjustment = variant.getPriceAdjustment() == null ? BigDecimal.ZERO : variant.getPriceAdjustment();
        return basePrice.add(adjustment);
    }
}
