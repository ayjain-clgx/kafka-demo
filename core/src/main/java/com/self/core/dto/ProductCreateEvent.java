package com.self.core.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductCreateEvent {

    private String id;
    private String name;

}
