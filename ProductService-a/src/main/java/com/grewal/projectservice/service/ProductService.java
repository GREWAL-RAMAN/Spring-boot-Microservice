package com.grewal.projectservice.service;

import com.grewal.projectservice.dto.ProductRequest;
import com.grewal.projectservice.dto.ProductResponse;
import com.grewal.projectservice.exception.ListNotFound;
import com.grewal.projectservice.model.Product;
import com.grewal.projectservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;


    public void createProduct(ProductRequest productRequest,String application)
    {
        Product product =Product.builder()
                .name(productRequest.getName().toLowerCase())
                .description(productRequest.getDescription())
                .applicationId(application)
                .type(productRequest.getType().toLowerCase())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Product {} is saved",product.getId());
    }



    public ProductResponse getProduct(String name,String application) throws Exception {
        Optional<Product> result=productRepository.findByApplicationIdAndName(application,name);
        if(result.isPresent())
        {
            return mapToProductResponse(result.get());
        }
        else{
            throw new Exception("Product does not exist");
        }
    }
    public void updateProduct(ProductRequest productRequest,String application,String id) {
        Optional<Product> result=productRepository.findByApplicationIdAndId(application,id);
        Product m=Product.builder().name(productRequest.getName().toLowerCase()).description(productRequest.getDescription())
                .price(productRequest.getPrice()).build();
        if(result.isPresent())
        {
            Product aa= result.get();
            aa.setName(m.getName().toLowerCase());
            aa.setDescription(m.getDescription());
            aa.setPrice(m.getPrice());
            aa.setType(m.getType().toLowerCase());
            productRepository.save(aa);
        }
        else{
            productRepository.save(m);
        }
    }

    public List<ProductResponse> getProductListByType(String type, int page) throws ListNotFound {
        Pageable pageable=PageRequest.of(page,10);

        Optional<List<Product>> list=productRepository.findAllByType(type.toLowerCase(),pageable);
        if(list.isPresent())
        {
            return list.get().stream().map(this::mapToProductResponse).toList();
        }
        else{
            throw new ListNotFound();
        }

    }


    public List<ProductResponse> getAllProducts(String application ,Integer page) throws ListNotFound {

        Pageable pageable=PageRequest.of(page,10);
        log.info("{} is the application",application);
        Optional<List<Product>> optProductList =productRepository.findAllByApplicationId(application,pageable);

        if(optProductList.isPresent())
        {
            List<Product> productList =optProductList.get();
            if(productList.isEmpty())
            {
                log.warn("List is empty");
            }
            return productList.stream().map(this::mapToProductResponse).toList();
        }
        else{
            throw new ListNotFound();
        }
    }

    private ProductResponse mapToProductResponse(Product product) {
    return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName().toLowerCase())
            .description(product.getDescription())
            .type(product.getType().toLowerCase())
            .price(product.getPrice())
            .build();
    }

    public boolean deleteProduct(String id,String application)
    {
        return productRepository.deleteByApplicationIdAndId(application,id);
    }


}
