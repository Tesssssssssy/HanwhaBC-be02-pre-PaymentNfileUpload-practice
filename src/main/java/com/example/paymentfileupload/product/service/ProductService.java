package com.example.paymentfileupload.product.service;

import com.example.paymentfileupload.product.model.Product;
import com.example.paymentfileupload.product.model.dto.ProductDtoReq;
import com.example.paymentfileupload.product.model.dto.ProductDtoRes;
import com.example.paymentfileupload.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(ProductDtoReq productDtoReq) {
        productRepository.save(Product.builder()
                .name(productDtoReq.getName())
                .price(productDtoReq.getPrice())
                .build());
    }

    public List<ProductDtoRes> findProductList() {
        List<Product> result = productRepository.findAll();
        List<ProductDtoRes> productDtoResList = new ArrayList<>();

        for (Product product : result) {
            ProductDtoRes productDtoRes = ProductDtoRes.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .build();
            productDtoResList.add(productDtoRes);
        }
        return productDtoResList;
    }
}
