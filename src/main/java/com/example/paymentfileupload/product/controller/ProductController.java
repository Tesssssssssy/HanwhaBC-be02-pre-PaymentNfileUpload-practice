package com.example.paymentfileupload.product.controller;

import com.example.paymentfileupload.product.model.dto.ProductDtoReq;
import com.example.paymentfileupload.product.model.dto.ProductDtoRes;
import com.example.paymentfileupload.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ResponseEntity createProduct(ProductDtoReq productDtoReq) {
        productService.createProduct(productDtoReq);
        return ResponseEntity.ok().body("Product 생성 완료");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public ResponseEntity findProductList() {
        List<ProductDtoRes> productDtoResList = productService.findProductList();
        return ResponseEntity.ok().body(productDtoResList);
    }
}
