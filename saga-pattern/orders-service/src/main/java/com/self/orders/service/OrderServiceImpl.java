package com.self.orders.service;

import com.self.core.dto.Order;
import com.self.core.dto.events.OrderApprovedEvent;
import com.self.core.dto.events.OrderCreatedEvent;
import com.self.core.dto.events.OrderRejectEvent;
import com.self.core.types.OrderStatus;
import com.self.orders.dao.jpa.entity.OrderEntity;
import com.self.orders.dao.jpa.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${order.events.topic.name}")
    private String orderEventsTopicName;

    @Override
    public Order placeOrder(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setCustomerId(order.getCustomerId());
        entity.setProductId(order.getProductId());
        entity.setProductQuantity(order.getProductQuantity());
        entity.setStatus(OrderStatus.CREATED);
        var savedEntity = orderRepository.save(entity);

        var orderCreatedEvent = new OrderCreatedEvent(
                savedEntity.getId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getProductQuantity()
        );

        kafkaTemplate.send(orderEventsTopicName, orderCreatedEvent);
        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getProductId(),
                entity.getProductQuantity(),
                entity.getStatus());
    }

    @Override
    public void approveOrder(UUID orderId) {
        var orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        orderEntity.setStatus(OrderStatus.APPROVED);
        orderRepository.save(orderEntity);

        var orderApprovedEvent = new OrderApprovedEvent(orderEntity.getId());
        kafkaTemplate.send(orderEventsTopicName, orderApprovedEvent);
    }

    @Override
    public void rejectOrder(UUID orderId) {
        var orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        orderEntity.setStatus(OrderStatus.REJECTED);
        orderRepository.save(orderEntity);

        var orderRejectEvent = new OrderRejectEvent(orderEntity.getId());
        kafkaTemplate.send(orderEventsTopicName, orderRejectEvent);
    }

}
