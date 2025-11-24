package com.self.kafkademo.controller;

import com.self.kafkademo.dto.ProductCreateEvent;
import com.self.kafkademo.model.Product;
import com.self.kafkademo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        var productCreated = productService.addProduct(product);
        return new ResponseEntity<>(productCreated, HttpStatus.CREATED);
    }
}
