package com.self.core.dto.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReserveProductCommand {
    private UUID orderId;
    private UUID productId;
    private Integer productQuantity;
}
