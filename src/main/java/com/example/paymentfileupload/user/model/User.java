package com.example.paymentfileupload.user.model;

import com.example.paymentfileupload.user.model.request.PostCreateUserReq;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(length = 30)
    private String name;

    @Column(length = 200, unique = true)
    private String image;

    public static User toEntity(PostCreateUserReq postCreateUserReq) {
        return User.builder()
                .email(postCreateUserReq.getEmail())
                .password(postCreateUserReq.getPassword())
                .name(postCreateUserReq.getName())
                .image(postCreateUserReq.getImage().getOriginalFilename())
                .build();

    }
}
