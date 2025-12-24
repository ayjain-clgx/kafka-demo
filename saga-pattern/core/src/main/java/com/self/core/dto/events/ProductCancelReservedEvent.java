package com.self.core.dto.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductCancelReservedEvent {
    private UUID productId;
    private UUID orderId;
    private Integer productQuantity;
}
