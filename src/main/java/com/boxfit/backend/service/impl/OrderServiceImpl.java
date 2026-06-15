package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.domain.entity.Address;
import com.boxfit.backend.domain.entity.Cart;
import com.boxfit.backend.domain.entity.CartItem;
import com.boxfit.backend.domain.entity.Order;
import com.boxfit.backend.domain.entity.OrderItem;
import com.boxfit.backend.domain.entity.Product;
import com.boxfit.backend.domain.entity.ProductVariant;
import com.boxfit.backend.domain.entity.User;
import com.boxfit.backend.domain.enums.OrderStatus;
import com.boxfit.backend.dto.request.order.CheckoutRequest;
import com.boxfit.backend.dto.request.order.OrderStatusUpdateRequest;
import com.boxfit.backend.dto.request.order.OrderTrackingUpdateRequest;
import com.boxfit.backend.dto.request.order.StaffOrderFilterRequest;
import com.boxfit.backend.dto.response.order.OrderItemResponse;
import com.boxfit.backend.dto.response.order.OrderResponse;
import com.boxfit.backend.repository.AddressRepository;
import com.boxfit.backend.repository.CartItemRepository;
import com.boxfit.backend.repository.CartRepository;
import com.boxfit.backend.repository.OrderRepository;
import com.boxfit.backend.repository.ProductVariantRepository;
import com.boxfit.backend.repository.specification.OrderSpecifications;
import com.boxfit.backend.service.OrderService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            AddressRepository addressRepository,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            ProductVariantRepository productVariantRepository) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public OrderResponse checkout(CheckoutRequest request) {
        User user = currentUser();
        Cart cart = cartRepository.findByUserId(user.getId())
            .orElseThrow(() -> new AppException(ErrorCode.CART_EMPTY, "Cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY, "Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        applyShippingSnapshot(order, request, user);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingFee(safeMoney(request.getShippingFee()));
        order.setDiscount(safeMoney(request.getDiscount()));

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = productVariantRepository.findById(cartItem.getVariant().getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND, "Product variant not found"));
            validateVariantForCheckout(variant, cartItem.getQuantity());

            Product product = variant.getProduct();
            BigDecimal unitPrice = calculateUnitPrice(product, variant);
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(totalPrice);

            variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
            productVariantRepository.save(variant);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setVariantId(variant.getId());
            item.setProductName(product.getName());
            item.setImageUrl(product.getImageUrl());
            item.setSku(variant.getSku());
            item.setSize(variant.getSize());
            item.setColor(variant.getColor());
            item.setUnitPrice(unitPrice);
            item.setQuantity(cartItem.getQuantity());
            item.setTotalPrice(totalPrice);
            order.getItems().add(item);
        }

        order.setSubtotal(subtotal);
        order.setTotal(subtotal.add(order.getShippingFee()).subtract(order.getDiscount()));
        Order saved = orderRepository.save(order);
        cartItemRepository.deleteByCartId(cart.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(currentUser().getId(), pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(UUID orderId) {
        return orderRepository.findByIdAndUserId(orderId, currentUser().getId())
            .map(this::toResponse)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));
    }

    @Override
    public OrderResponse cancelMyOrder(UUID orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, currentUser().getId())
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS, "Only pending orders can be canceled");
        }
        order.setStatus(OrderStatus.CANCELED);
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getStaffOrders(StaffOrderFilterRequest filter, Pageable pageable) {
        StaffOrderFilterRequest safeFilter = filter == null ? new StaffOrderFilterRequest() : filter;
        Specification<Order> spec = Specification.where(OrderSpecifications.statusEquals(safeFilter.getStatus()))
            .and(OrderSpecifications.createdFrom(safeFilter.getCreatedFrom()))
            .and(OrderSpecifications.createdTo(safeFilter.getCreatedTo()))
            .and(OrderSpecifications.keywordContains(safeFilter.getKeyword()));
        return orderRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getStaffOrder(UUID orderId) {
        return toResponse(findOrder(orderId));
    }

    @Override
    public OrderResponse updateStatus(UUID orderId, OrderStatusUpdateRequest request) {
        Order order = findOrder(orderId);
        validateStatusTransition(order.getStatus(), request.getStatus());
        order.setStatus(request.getStatus());
        return toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse updateTracking(UUID orderId, OrderTrackingUpdateRequest request) {
        Order order = findOrder(orderId);
        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS, "Cannot update tracking for completed or canceled order");
        }
        order.setTrackingCode(request.getTrackingCode().trim());
        return toResponse(orderRepository.save(order));
    }

    private User currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "Not authenticated");
        }
        return user;
    }

    private Order findOrder(UUID orderId) {
        return orderRepository.findWithItemsById(orderId)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));
    }

    private void applyShippingSnapshot(Order order, CheckoutRequest request, User user) {
        if (request.getAddressId() != null) {
            Address address = addressRepository.findByIdAndUserId(request.getAddressId(), user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, "Address not found"));
            order.setRecipientName(address.getRecipientName());
            order.setPhoneNumber(address.getPhoneNumber());
            order.setShippingAddress(toFullAddress(address));
            return;
        }

        if (!StringUtils.hasText(request.getRecipientName())
            || !StringUtils.hasText(request.getPhoneNumber())
            || !StringUtils.hasText(request.getShippingAddress())) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Shipping information is required");
        }
        order.setRecipientName(request.getRecipientName().trim());
        order.setPhoneNumber(request.getPhoneNumber().trim());
        order.setShippingAddress(request.getShippingAddress().trim());
    }

    private String toFullAddress(Address address) {
        return String.join(", ",
            address.getStreetAddress(),
            address.getWard(),
            address.getDistrict(),
            address.getProvince()
        );
    }

    private void validateVariantForCheckout(ProductVariant variant, int quantity) {
        if (!variant.isActive()) {
            throw new AppException(ErrorCode.PRODUCT_VARIANT_INACTIVE, "Product variant is inactive");
        }
        if (quantity <= 0) {
            throw new AppException(ErrorCode.INVALID_QUANTITY, "Quantity must be greater than 0");
        }
        if (variant.getStockQuantity() < quantity) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK, "Insufficient stock");
        }
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.COMPLETED || current == OrderStatus.CANCELED) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS, "Cannot update completed or canceled order");
        }
        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELED;
            case CONFIRMED -> next == OrderStatus.PACKING || next == OrderStatus.CANCELED;
            case PACKING -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.COMPLETED;
            default -> false;
        };
        if (!valid) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS, "Invalid order status transition");
        }
    }

    private BigDecimal calculateUnitPrice(Product product, ProductVariant variant) {
        BigDecimal basePrice = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
        BigDecimal adjustment = variant.getPriceAdjustment() == null ? BigDecimal.ZERO : variant.getPriceAdjustment();
        return basePrice.add(adjustment);
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setCustomerEmail(order.getUser().getEmail());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setRecipientName(order.getRecipientName());
        response.setPhoneNumber(order.getPhoneNumber());
        response.setShippingAddress(order.getShippingAddress());
        response.setTrackingCode(order.getTrackingCode());
        response.setSubtotal(order.getSubtotal());
        response.setShippingFee(order.getShippingFee());
        response.setDiscount(order.getDiscount());
        response.setTotal(order.getTotal());
        response.setItems(order.getItems().stream().map(this::toItemResponse).toList());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setVariantId(item.getVariantId());
        response.setProductName(item.getProductName());
        response.setImageUrl(item.getImageUrl());
        response.setSku(item.getSku());
        response.setSizeName(item.getSize() == null ? null : item.getSize().name());
        response.setColorName(item.getColor());
        response.setUnitPrice(item.getUnitPrice());
        response.setQuantity(item.getQuantity());
        response.setTotalPrice(item.getTotalPrice());
        return response;
    }
}
