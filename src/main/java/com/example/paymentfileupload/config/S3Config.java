package com.example.paymentfileupload.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class S3Config {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     *  왜 @Bean으로 등록?
     *  여기서 생성해서 반환하는 객체는 내가 만들어서 반환하는 것이 아니니까
     *  @Bean으로 등록한다.
     *
     * @Bean으로 등록해놓고 upload할 때 사용한다.
     *
     *  처음 프로젝트 생성할 때 Bean 생성하는데
     *  Amazons3가 여러 개가 있어서 에러 발생했었음.
     *  그래서 @Primary annotation 을 달아준다. (우선 순위)
     */
    @Primary
    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return (AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

}
