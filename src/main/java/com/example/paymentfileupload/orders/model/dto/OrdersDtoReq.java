package com.example.paymentfileupload.orders.model.dto;

import com.example.paymentfileupload.product.model.dto.ProductDtoReq;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersDtoReq {
    private List<ProductDtoReq> OrdersProducts;
}
