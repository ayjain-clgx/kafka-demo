package com.self.core.dto.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReservedEvent {
    private UUID productId;
    private UUID orderId;
    private BigDecimal productPrice;
    private Integer productQuantity;

}
