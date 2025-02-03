package com.grewal.orderservice.service;


import com.grewal.orderservice.dto.*;
import com.grewal.orderservice.model.Order;
import com.grewal.orderservice.model.orderLineItems;
import com.grewal.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    public OrderResponse placeOrder(OrderRequest orderRequest, String application)
    {
        Order order =new Order();
        order.setOrderedBy(orderRequest.getOrderedBy());
        order.setOrderNumber(UUID.randomUUID().toString());


       List<orderLineItems> orderLineItemsList= orderRequest
                .getOrderLineItemDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();


        Map<String, orderLineItems> orderLineItemsMap = orderRequest
                .getOrderLineItemDtoList()
                .stream()
                .map(this::mapToDto) // Assuming this method converts DTO to OrderLineItem
                .collect(Collectors.toMap(orderLineItems::getSkuCode, Function.identity()));


        List<InventoryDto> sk=orderLineItemsList.stream()
                .map(m-> InventoryDto.builder()
                        .skuCode(m.getSkuCode())
                        .quantity(m.getQuantity())
                        .build()).toList();



        List<InventoryResponse> inventoryResponses= webClientBuilder.
                build()
                .post()
                .uri("http://inventory-service-sec/api/inventory/check")
                .header("id",application)
                .bodyValue(sk)
               .retrieve()
               .bodyToMono(new ParameterizedTypeReference<List<InventoryResponse>>() {
               })
               .block();

        List<InventoryDto> placeInventory=new ArrayList<>();

       List<orderLineItems> toBePlaced=new ArrayList<>();
        List<String> toBeNotPlaced=new ArrayList<>();
       for(InventoryResponse m:inventoryResponses)
       {
           if(m.isInStock())
           {
               toBePlaced.add(orderLineItemsMap.get(m.getSkuCode()));
               placeInventory.add(InventoryDto.builder().skuCode(m.getSkuCode()).quantity(m.getQuantity()).build());
           }
           else{
               toBeNotPlaced.add(m.getSkuCode());
           }
       }

       if(!toBePlaced.isEmpty())
       {
           order.setApplicationId(application);
           order.setOrderLineItemsList(toBePlaced);
           orderRepository.save(order);
           webClientBuilder.
                   build()
                   .post()
                   .uri("http://inventory-service/api/inventory/order")
                   .header("id",application)
                   .bodyValue(placeInventory).retrieve().toEntity(String.class).block();


           if(toBeNotPlaced.isEmpty())
           {
               return OrderResponse.builder()
                       .allProductInStock(true)
                       .message("All Products order placed ").orderNotPlaced(Collections.emptyList()).build();
           }
           else{
               return OrderResponse.builder()
                       .allProductInStock(false)
                       .message("All Products order was not placed as they were not in stock ")
                       .orderNotPlaced(toBeNotPlaced).build();
           }
       }
       else{
           return OrderResponse.builder()
                   .allProductInStock(false)
                   .message("Not a single product in stocks").allProductInStock(false)
                   .orderNotPlaced(null).build();
       }

    }

    private orderLineItems mapToDto(OrderLineItemDto orderLineItemDto) {
        orderLineItems orderLineItems=new orderLineItems();
        orderLineItems.setPrice(orderLineItemDto.getPrice());
        orderLineItems.setQuantity(orderLineItemDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemDto.getSkuCode());

        return orderLineItems;
     }
    public List<ResponseOrder> getAllProducts(String application ,Integer page) throws Exception {

        Pageable pageable= PageRequest.of(page,10);
        Optional<List<Order>> optProductList =orderRepository.findAllByApplicationId(application,pageable);

        if(optProductList.isPresent())
        {
            List<Order> productList =optProductList.get();
            return productList.stream().map(this::mapToProductResponse).toList();
        }
        else{
            throw new Exception("The order for this application was not found");
        }
    }

    private ResponseOrder mapToProductResponse(Order order) {
        return ResponseOrder.builder()
                .orderedBy(order.getOrderedBy())
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderLineItemsList(this.mapToResponseLine(order.getOrderLineItemsList()))
                .build();
    }

    private List<ResponseLine> mapToResponseLine(List<orderLineItems> list )
    {
        List<ResponseLine> line=new ArrayList<>();

        for(orderLineItems l:list)
        {
            line.add(ResponseLine.builder()
                    .skuCode(l.getSkuCode())
                    .quantity(l.getQuantity())
                    .price(l.getPrice()).build());
        }
        return line;
    }
}
