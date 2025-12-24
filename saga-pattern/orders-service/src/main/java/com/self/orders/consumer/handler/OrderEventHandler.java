package com.self.orders.consumer.handler;

import com.self.core.dto.commands.ApproveOrderCommands;
import com.self.core.dto.commands.RejectOrderCommand;
import com.self.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {
        "${order.commands.topic.name}"
}, groupId = "${spring.kafka.consumer.group-id}")
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

    private final OrderService orderService;

    @KafkaHandler
    public void handleApproveOrderCommandEvent(@Payload ApproveOrderCommands approveOrderCommands) {
        log.info("Received approve order command event: {}", approveOrderCommands);

        orderService.approveOrder(approveOrderCommands.getOrderId());
    }

    @KafkaHandler
    public void handleRejectOrderCommandEvent(@Payload RejectOrderCommand rejectOrderCommand) {
        log.info("Received reject order command event: {}", rejectOrderCommand);

        orderService.rejectOrder(rejectOrderCommand.getOrderId());
    }
}
