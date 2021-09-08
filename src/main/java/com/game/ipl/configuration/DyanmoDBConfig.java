package com.game.ipl.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.game.ipl.repositories")
public class DyanmoDBConfig {
    @Value("${aws.access.key}")
    private String accessKey;
    @Value("${aws.secret.key}")
    private String secretKey;
    @Value("${aws.dynamodb-local.endpoint}")
    private String endpoint;
    @Value("${aws.region}")
    private String region;

    @Bean
    @ConditionalOnProperty(
            value = "aws.dynamodb-local.enabled",
            havingValue = "false")
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials())).withRegion(region).build();
    }

    @Bean
    @ConditionalOnProperty(
            value = "aws.dynamodb-local.enabled",
            havingValue = "false")
    public AWSCredentials awsCredentials() {
        new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    @ConditionalOnProperty(
            value = "aws.dynamodb-local.enabled",
            havingValue = "true")
    public AmazonDynamoDB localAmazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build();
    }
}
