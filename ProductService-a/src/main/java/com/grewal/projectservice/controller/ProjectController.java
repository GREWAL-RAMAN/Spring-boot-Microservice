package com.grewal.projectservice.controller;

import com.grewal.projectservice.dto.ProductRequest;
import com.grewal.projectservice.dto.ProductResponse;
import com.grewal.projectservice.exception.ListNotFound;
import com.grewal.projectservice.service.ProductService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    private final ProductService productService;

    @PostMapping("/user/product")
    @ResponseStatus(HttpStatus.CREATED)
    public String  createProduct(@RequestBody ProductRequest productRequest,@RequestHeader("id") String application)
    {

        log.info("create product called {}",productRequest);
        log.info("create product for application - {}",application);
        productService.createProduct(productRequest,application);
        return productRequest.getName();
    }



    @DeleteMapping("/user/product/{productId}")
    public ResponseEntity<String> deleteProduct(@RequestHeader("id") String application, @PathVariable("productId")
                                              String id )
    {
        boolean m=productService.deleteProduct(id,application);
        if(m)
        {
            return ResponseEntity.ok("The product was deleted");
        }
        else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("The product was not deleted");
        }

    }

    @PutMapping("/user/product/{productId}")
    public void updateProduct(@RequestHeader("id") String application,
                                                @RequestBody ProductRequest productRequest
     , @PathVariable("productId") String productId)
             {
        productService.updateProduct(productRequest,application,productId);
    }

    @GetMapping("/product/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProductResponse> getProductByName(@RequestHeader("id") String application,
                                            @PathVariable("name") String name)
            throws Exception {
        System.out.println("thiis sis product/name/{name}");
        System.out.println(application);


        return ResponseEntity.ok(productService.getProduct(name,application));
    }

    @GetMapping("/product/type/{type}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ProductResponse>> getProductByType(@RequestHeader("id") String application,
                                            @PathVariable(value = "page",required = false) Integer page,
                                                  @PathVariable("type") String type
     )
            throws Exception {
        if(page == null )
        {
            page=0;
        }
        return ResponseEntity.ok(productService.getProductListByType(type,page));
    }
    @GetMapping("/product/get/{page}")
    public ResponseEntity<List<ProductResponse>> getAllProducts(@RequestHeader("id") String application,
                                                                @PathVariable(value = "page",required = false)Integer page)
            throws ListNotFound {
        if(page == null )
        {
            page=0;
        }

        return ResponseEntity.ok( productService.getAllProducts(application,page));

    }

    @ExceptionHandler(Exception.class)
    public String getException(Exception a)
    {
        return a.getMessage();
    }
}
