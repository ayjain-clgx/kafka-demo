package com.self.products.controller;

import com.self.products.model.Product;
import com.self.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<String> apiForEmailService() {
        return ResponseEntity.ok("Hello from Product Service");
    }
}
