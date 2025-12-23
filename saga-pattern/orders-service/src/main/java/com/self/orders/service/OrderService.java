package com.self.orders.service;

import com.self.core.dto.Order;

import java.util.UUID;

public interface OrderService {
    Order placeOrder(Order order);
    void approveOrder(UUID orderId);
}
