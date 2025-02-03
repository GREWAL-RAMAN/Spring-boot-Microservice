package com.grewal.orderservice.dto;

import lombok.*;

import java.util.List;


@Data

@Builder
public class OrderResponse {
    Boolean allProductInStock;
    String message;
    List<String> orderNotPlaced;
}
