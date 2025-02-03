package com.grewal.inventory.inventoryservice.Controller;

import com.grewal.inventory.inventoryservice.Service.InventoryService;
import com.grewal.inventory.inventoryservice.dto.InventoryDto;
import com.grewal.inventory.inventoryservice.dto.InventoryResponse;
import com.grewal.inventory.inventoryservice.model.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/inventory/check")
    @ResponseStatus(HttpStatus.OK)

  private List<InventoryResponse> isInStock(@RequestBody() List<InventoryDto> skucode,
                                            @RequestHeader("id") String application)
    {
        return inventoryService.checkInventoryStatus(skucode,application);
    }

    @PostMapping("/inventory/order")
    @ResponseStatus(HttpStatus.OK)

    private String  orderPlacedEnd(@RequestBody() List<InventoryDto> skucode,
                                              @RequestHeader("id") String application)
    {

        inventoryService.PlaceOrderList(skucode,application);
        return "Placed";
    }

    @GetMapping("/inventory/{page}")
    public ResponseEntity<List<InventoryResponse>> getAllProducts(@RequestHeader("id") String application,
                                                                @PathVariable(value = "page",required = false)Integer page)
            throws Exception {
        if(page == null )
        {
            page=0;
        }

        return ResponseEntity.ok( inventoryService.getAllProducts(application,page));

    }

    @GetMapping("/inventory/get")
    public ResponseEntity<InventoryResponse> getProduct(@RequestHeader("id") String application,
                                                                  @RequestParam(required = true)String name)
            throws Exception {

        return ResponseEntity.ok( inventoryService.getProduct(application,name));

    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String getException(Exception a)
    {
        return a.getMessage();
    }



    @PostMapping("/user/inventory")
    @ResponseStatus(HttpStatus.CREATED)
    private String addStock(@RequestBody InventoryDto inventoryDto,
    @RequestHeader("id") String application)
    {
        inventoryService.addStock(inventoryDto,application);
        return "The stock has been added as specified";
    }

    @DeleteMapping("/user/inventory")
    @ResponseStatus(HttpStatus.OK)
    private String removeStock(@RequestBody InventoryDto inventoryDto,
                            @RequestHeader("id") String application)
    {
        inventoryService.removeStock(inventoryDto,application);
        return "The stock quantity has been appropriately removed";
    }

}
