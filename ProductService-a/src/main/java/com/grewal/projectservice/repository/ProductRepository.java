package com.grewal.projectservice.repository;

import com.grewal.projectservice.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product,String> {


    Optional<List<Product>> findAllByApplicationId(String application,Pageable pageable);
    Optional<Product> findByApplicationIdAndName(String applicationId,String name);
    Optional<Product> findByApplicationIdAndId(String applicationId,String id);
    Optional<List<Product>> findAllByType(String type, Pageable pageable);


    boolean deleteByApplicationIdAndId(String application,String id);
}
