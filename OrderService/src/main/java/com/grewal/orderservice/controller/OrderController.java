package com.grewal.orderservice.controller;

import com.grewal.orderservice.dto.OrderRequest;
import com.grewal.orderservice.dto.OrderResponse;
import com.grewal.orderservice.dto.ResponseOrder;
import com.grewal.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PostMapping("/order")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory" , fallbackMethod = "fallbackTimeMethod")
    public CompletableFuture<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest,
                                                       @RequestHeader("id") String application)
    {


        return CompletableFuture.supplyAsync(()->orderService.placeOrder(orderRequest,application));
    }

    @GetMapping("/user/{page}")
    public ResponseEntity<List<ResponseOrder>> getAllProducts(@RequestHeader("id") String application,
                                                              @PathVariable(value = "page",required = false)Integer page)
            throws Exception {
        if(page == null )
        {
            page=0;
        }

        return ResponseEntity.ok( orderService.getAllProducts(application,page));

    }


    public CompletableFuture<OrderResponse> fallbackMethod(OrderRequest orderRequest,String application, Throwable exception)
    {
        return CompletableFuture.supplyAsync(()->OrderResponse.builder()
                .message("""
                Bhai koi Dikat hog hai!\s
                Inventory service kaam ni kar rahi!
                Jake order_controller me dekh!
                Circuit breaker ne fallback logic apply kar diya hai.
                """).allProductInStock(false).orderNotPlaced(null).build());
    }
    @ExceptionHandler(Exception.class)
    public String getException(Exception a)
    {
        return a.getMessage();
    }



    public CompletableFuture<OrderResponse> fallbackTimeMethod(OrderRequest orderRequest,String application, Throwable exception)
    {
        return CompletableFuture.supplyAsync(()->OrderResponse.builder()
                .message("""
                Bhai koi Dikat hog hai!\s
                Inventory service kaam ni kar rahi!
                Jake order_controller me dekh!
                Circuit breaker ne fallback logic apply kar diya hai.
                """).allProductInStock(false).orderNotPlaced(null).build());
    }
}
