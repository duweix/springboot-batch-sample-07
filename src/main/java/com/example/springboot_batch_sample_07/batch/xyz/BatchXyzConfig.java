package com.example.springboot_batch_sample_07.batch.xyz;

import java.util.Calendar;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class BatchXyzConfig {

    @Autowired
    private JobBuilderFactory jobs;
    @Autowired
    private JobLauncher jobLauncher;

    @Bean
    protected Job batchXyzMainJob(JobRepository jobRepository, Step batchXyzStep1, Step batchXyzStep2) {
        // @formatter:off
        return jobs.get("batchXyzMainJob")
                .repository(jobRepository)
                .start(batchXyzStep1)
                .next(batchXyzStep2)
                .build();
        // @formatter:on
    }

    @Bean
    protected TaskExecutor asyncTaskExecutor() {
        return new SimpleAsyncTaskExecutor("batchXyzAsyncTaskExecutor");
    }

    public void exec() {
        ((SimpleJobLauncher) jobLauncher).setTaskExecutor(asyncTaskExecutor());
        try {
            jobLauncher.run(batchXyzMainJob(null, null, null), new JobParametersBuilder().addDate("START", Calendar.getInstance().getTime()).toJobParameters());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
