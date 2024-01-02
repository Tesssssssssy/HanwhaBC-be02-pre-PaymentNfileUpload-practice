package com.example.paymentfileupload.user.model.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateUserRes {
    private Long id;
    private String email;
    private String name;
    private String image;
}
