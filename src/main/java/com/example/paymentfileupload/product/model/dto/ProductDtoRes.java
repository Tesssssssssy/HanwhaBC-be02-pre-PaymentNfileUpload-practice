package com.example.paymentfileupload.product.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDtoRes {
    private Long id;
    private String name;
    private Integer price;
}
