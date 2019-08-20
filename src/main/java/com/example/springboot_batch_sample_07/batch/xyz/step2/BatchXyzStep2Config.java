package com.example.springboot_batch_sample_07.batch.xyz.step2;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.BindException;

import com.example.springboot_batch_sample_07.batch.xyz.entity.AnotherDbTableEntity;
import com.example.springboot_batch_sample_07.batch.xyz.entity.FileDataItemEntity;
import com.example.springboot_batch_sample_07.batch.xyz.entity.MultiExecMgrEntity;

@Configuration
public class BatchXyzStep2Config {

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    protected Step batchXyzStep2(PlatformTransactionManager transactionManager) {
        // @formatter:off
        return steps.get("batchXyzStep2")
                .partitioner("batchXyzStep2Partitioner", batchXyzStep2Partitioner(null))
                .step(batchXyzStep2SlaverStep(null))
                .build();
        // @formatter:on
    }

    @Bean
    @StepScope
    protected Partitioner batchXyzStep2Partitioner(@Value("#{jobExecutionContext[entityList]}") List<MultiExecMgrEntity> entityList) {
        BatchXyzStep2Partitioner partitioner = new BatchXyzStep2Partitioner();
        partitioner.setEntityList(entityList);
        return partitioner;
    }

    @Bean
    @StepScope
    protected Step batchXyzStep2SlaverStep(PlatformTransactionManager transactionManager) {
        // @formatter:off
        return steps.get("batchXyzStep2SlaverStep")
                .transactionManager(transactionManager)
                .<FileDataItemEntity, AnotherDbTableEntity>chunk(100)
                .reader(batchXyzStep2MultiResourceReader(null))
                .processor(batchXyzStep2Processor())
                .writer(batchXyzStep2Writer())
                .build();
        // @formatter:on
    }

    @Bean
    @StepScope
    protected MultiResourceItemReader<FileDataItemEntity> batchXyzStep2MultiResourceReader(@Value("#{stepExecutionContext[partitionedEntityList]}") List<MultiExecMgrEntity> partitionedEntityList) {
        List<String> filesName = partitionedEntityList.stream().map(entity -> "E:\\prefecture-csv-files\\" + entity.getProcTarget() + ".csv").collect(Collectors.toList());
        Resource[] resources = filesName.stream().map(FileSystemResource::new).collect(Collectors.toList()).toArray(new Resource[0]);
        // @formatter:off
        return new MultiResourceItemReaderBuilder<FileDataItemEntity>()
                .name("batchXyzStep2MultiResourceReader")
                .delegate(batchXyzStep2SingleResourceReader())
                .resources(resources)
                .build();
        // @formatter:on
    }

    @Bean
    @StepScope
    protected FlatFileItemReader<FileDataItemEntity> batchXyzStep2SingleResourceReader() {
        // @formatter:off
        return new FlatFileItemReaderBuilder<FileDataItemEntity>()
                .name("batchXyzStep2SingleResourceReader")
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new FieldSetMapper<FileDataItemEntity>() {

                    @Override
                    public FileDataItemEntity mapFieldSet(FieldSet fieldSet) throws BindException {
                        FileDataItemEntity entity = new FileDataItemEntity();
                        entity.setField1(fieldSet.readString(0));
                        entity.setField2(fieldSet.readString(1));
                        entity.setField3(fieldSet.readString(2));
                        return entity;
                    }

                })
                .build();
        // @formatter:on
    }

    @Bean
    @StepScope
    protected FunctionItemProcessor<FileDataItemEntity, AnotherDbTableEntity> batchXyzStep2Processor() {
        return new FunctionItemProcessor<FileDataItemEntity, AnotherDbTableEntity>(multiExecMgrEntity -> {
            AnotherDbTableEntity anotherDbTableEntity = new AnotherDbTableEntity();
            anotherDbTableEntity.setField1(multiExecMgrEntity.getField1());
            anotherDbTableEntity.setField2(multiExecMgrEntity.getField2());
            anotherDbTableEntity.setField3(multiExecMgrEntity.getField3());
            return anotherDbTableEntity;
        });
    }

    @Bean
    @StepScope
    protected ListItemWriter<AnotherDbTableEntity> batchXyzStep2Writer() {
        return new ListItemWriter<AnotherDbTableEntity>() {

            @Override
            public void write(List<? extends AnotherDbTableEntity> items) throws Exception {
                super.write(items);
                items.stream().forEach(System.out::println);
            }

        };
    }
}
