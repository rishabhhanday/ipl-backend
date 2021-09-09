package com.game.ipl.configuration;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordEncryptionConfiguration {
    @Bean
    BasicPasswordEncryptor passwordEncryptor(){
        return new BasicPasswordEncryptor();
    }
}
