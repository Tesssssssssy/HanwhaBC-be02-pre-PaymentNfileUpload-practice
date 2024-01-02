package com.example.paymentfileupload.orders.model.dto;

import com.example.paymentfileupload.product.model.dto.ProductDtoRes;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersDtoRes {
    private Long id;
    private List<ProductDtoRes> OrdersProducts;
}
