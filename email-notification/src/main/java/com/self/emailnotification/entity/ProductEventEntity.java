package com.self.emailnotification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "product_event")
@Getter
@Setter
@NoArgsConstructor
public class ProductEventEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String messageId;

    @Column(nullable = false)
    private String productId;

    public ProductEventEntity(String messageId, String productId) {
        this.messageId = messageId;
        this.productId = productId;
    }
}
