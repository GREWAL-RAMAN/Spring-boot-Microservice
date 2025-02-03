package com.grewal.orderservice.dto;

import com.grewal.orderservice.model.orderLineItems;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseOrder {

    private Long id;
    private String orderedBy;
    private  String orderNumber;

    private List<ResponseLine> orderLineItemsList;
}
