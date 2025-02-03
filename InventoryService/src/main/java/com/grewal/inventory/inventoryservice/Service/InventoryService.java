package com.grewal.inventory.inventoryservice.Service;

import com.grewal.inventory.inventoryservice.dto.InventoryDto;
import com.grewal.inventory.inventoryservice.dto.InventoryResponse;
import com.grewal.inventory.inventoryservice.model.Inventory;
import com.grewal.inventory.inventoryservice.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Comments;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor

public class InventoryService {

    private final InventoryRepository inventoryRepository;


    @Transactional
    public List<InventoryResponse> inStock(List<String> skuCode) {

        return inventoryRepository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder().
                                skuCode(inventory.getSkuCode())
                                .inStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();

    }

    public void addStock(InventoryDto inventoryDto, String application) {
        Optional<Inventory> stock = inventoryRepository.findBySkuCodeAndApplicationId(inventoryDto.getSkuCode(), application);
        if (stock.isPresent()) {
            Inventory a = stock.get();
            a.setQuantity(a.getQuantity() + inventoryDto.getQuantity());
            a.setApplicationId(application);
            inventoryRepository.save(a);
        } else {
            Inventory b = new Inventory();
            b.setSkuCode(inventoryDto.getSkuCode());
            b.setQuantity(inventoryDto.getQuantity());
            b.setApplicationId(application);
            inventoryRepository.save(b);
        }
    }
    public void removeStock(InventoryDto inventoryDto, String application) {
        Optional<Inventory> stock = inventoryRepository.findBySkuCodeAndApplicationId(inventoryDto.getSkuCode(), application);
        if (stock.isPresent()) {
            Inventory a = stock.get();
            a.setQuantity(a.getQuantity() - inventoryDto.getQuantity());
            if(a.getQuantity()<0)
            {
                a.setQuantity(0);
            }
            a.setApplicationId(application);
            inventoryRepository.save(a);
        }
    }

    public List<InventoryResponse> getAllProducts(String application ,Integer page) throws Exception {

        Pageable pageable= PageRequest.of(page,10);
        Optional<List<Inventory>> optProductList =inventoryRepository.findAllByApplicationId(application,pageable);

        if(optProductList.isPresent())
        {
            List<Inventory> productList =optProductList.get();
            return productList.stream().map(this::mapToProductResponse).toList();
        }
        else{
            throw new Exception("inventory for this application does not exists!!!");
        }
    }
    public InventoryResponse getProduct(String application ,String name) throws Exception {


       Optional<Inventory> inventory = inventoryRepository.findBySkuCodeAndApplicationId(name,application);

        if(inventory.isPresent())
        {

            Inventory result =inventory.get();

            return this.mapToProductResponse(result);
        }
        else{
            throw new Exception("inventory for this application and sKuCode does not exists!!!");
        }
    }
    private InventoryResponse mapToProductResponse(Inventory product) {
        return InventoryResponse.builder()
                .skuCode(product.getSkuCode())
                .quantity(product.getQuantity())
                .inStock(product.getQuantity()>0)
                .build();
    }

    @Transactional
    public List<InventoryResponse> checkInventoryStatus(List<InventoryDto> inventoryDtos, String application) {
        List<Inventory> allInventories = inventoryRepository.findAllByApplicationId(application); // Retrieve all inventories

        return inventoryDtos.stream()
                .map(dto -> {
                    Inventory matchingInventory = allInventories.stream()
                            .filter(inv -> inv.getSkuCode().equals(dto.getSkuCode()))
                            .findFirst()
                            .orElse(null); // Handle potential missing inventory

                    return new InventoryResponse(dto.getSkuCode(),
                            matchingInventory != null && matchingInventory.getQuantity() >= dto.getQuantity(), dto.getQuantity());
                })
                .collect(Collectors.toList());
    }




    @Transactional
    public void PlaceOrderList(List<InventoryDto> inventoryDtos, String application) {
        List<Inventory> allInventories = inventoryRepository.findAllByApplicationId(application); // Retrieve all inventories

       List<Inventory> rst= inventoryDtos.stream().map(dto->
                {
              Inventory a=  allInventories.stream().filter(gg->gg.getSkuCode().equals(dto.getSkuCode())).findFirst().get();
              a.setQuantity(a.getQuantity()- dto.getQuantity());

                return a;
                }
                ).toList();

       inventoryRepository.saveAll(rst);


    }

}
