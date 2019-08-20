package com.example.springboot_batch_sample_07.batch.xyz.step1;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.springboot_batch_sample_07.batch.xyz.entity.MultiExecMgrEntity;

@Configuration
public class BatchXyzStep1Config {

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    protected Step batchXyzStep1(PlatformTransactionManager transactionManager) {
        // @formatter:off
        return steps.get("batchXyzStep1")
                .transactionManager(transactionManager)
                .<MultiExecMgrEntity, MultiExecMgrEntity>chunk(100)
                .reader(batchXyzStep1Reader(null))
                .writer(batchXyzStep1Writer())
                .listener(batchXyzStep1PromotionListener())
                .build();
        // @formatter:on
    }

    @Bean
    protected JdbcCursorItemReader<MultiExecMgrEntity> batchXyzStep1Reader(DataSource dataSource) {
        // @formatter:off
        return new JdbcCursorItemReaderBuilder<MultiExecMgrEntity>()
                .name("batchXyzStep1Reader")
                .dataSource(dataSource)
                .sql("SELECT * FROM MULTI_EXEC_MGR")
                .rowMapper(new BeanPropertyRowMapper<MultiExecMgrEntity>(MultiExecMgrEntity.class))
                .build();
        // @formatter:on
    }

    @Bean
    protected ItemWriter<MultiExecMgrEntity> batchXyzStep1Writer() {
        return new ItemWriter<MultiExecMgrEntity>() {
            private StepExecution stepExecution;

            @Override
            public void write(List<? extends MultiExecMgrEntity> items) throws Exception {
                ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
                @SuppressWarnings("unchecked")
                List<MultiExecMgrEntity> entityList = (List<MultiExecMgrEntity>) stepExecutionContext.get("entityList");
                if (entityList == null) {
                    entityList = new ArrayList<>();
                    stepExecutionContext.put("entityList", entityList);
                }
                entityList.addAll(items);
            }

            @BeforeStep
            public void beforeStep(StepExecution stepExecution) {
                this.stepExecution = stepExecution;
            }
        };
    }

    @Bean
    protected ExecutionContextPromotionListener batchXyzStep1PromotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] { "entityList" });
        return listener;
    }
}
