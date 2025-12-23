package com.self.core.dto.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentCommand {
    private UUID orderId;
    private UUID productId;
    private Integer productQuantity;
    private BigDecimal productPrice;
}
