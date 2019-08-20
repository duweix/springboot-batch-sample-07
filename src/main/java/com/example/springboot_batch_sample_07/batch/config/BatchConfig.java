package com.example.springboot_batch_sample_07.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.GenericApplicationContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.springboot_batch_sample_07.batch.xyz.BatchXyzConfig;

@Configuration
@EnableBatchProcessing(modular = true)
public class BatchConfig {

    @Bean
    protected GenericApplicationContextFactory batchXyz() {
        return new GenericApplicationContextFactory(BatchXyzConfig.class);
    }
}
