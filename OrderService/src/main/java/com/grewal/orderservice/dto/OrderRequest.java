package com.grewal.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private String orderedBy;
    private List<OrderLineItemDto> orderLineItemDtoList;
}
