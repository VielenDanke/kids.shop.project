package kz.danke.shipment.service.configuration.batch;

import kz.danke.shipment.service.configuration.batch.policy.PurchaseSkipPolicy;
import kz.danke.shipment.service.mapper.PurchaseEntityRowMapper;
import kz.danke.shipment.service.model.Purchase;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Value("${purchases.db.reader_query}")
    private String readerQuery;
    @Value("${purchases.db.writer_query}")
    private String writerQuery;
    @Value("${purchases.file.path}")
    private String writerFilePath;

    @Bean("purchaseItemReader")
    @StepScope
    public ItemReader<Purchase> purchaseItemReader(
            DataSource dataSource
    ) {
        return new JdbcCursorItemReaderBuilder<Purchase>()
                .dataSource(dataSource)
                .name("purchaseReader")
                .sql(readerQuery)
                .rowMapper(new PurchaseEntityRowMapper())
                .build();
    }

    @Bean("purchaseItemWriter")
    @StepScope
    public JdbcBatchItemWriter<Purchase> purchaseItemWriter(
            DataSource dataSource
    ) {
        return new JdbcBatchItemWriterBuilder<Purchase>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(writerQuery)
                .build();
    }

    @Bean("purchaseFileItemWriter")
    public ItemWriter<Purchase> purchaseFileItemWriter() {
        return new FlatFileItemWriterBuilder<Purchase>()
                .name("purchaseFileItemWriter")
                .resource(new FileSystemResource(writerFilePath))
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }

    @Bean("compositePurchaseItemWriters")
    public ItemWriter<Purchase> compositePurchaseItemWriters(
            @Qualifier("purchaseItemWriter") ItemWriter<Purchase> purchaseItemWriter,
            @Qualifier("purchaseFileItemWriter") ItemWriter<Purchase> purchaseFileItemWriter
    ) {
        return new CompositeItemWriterBuilder<Purchase>()
                .delegates(purchaseItemWriter, purchaseFileItemWriter)
                .build();
    }

    @Bean("purchaseStep")
    public Step purchaseStep(
            @Qualifier("purchaseItemReader") ItemReader<Purchase> purchaseItemReader,
            @Qualifier("compositePurchaseItemWriters") ItemWriter<Purchase> compositePurchaseItemWriter,
            @Qualifier("transactionManager") PlatformTransactionManager transactionManager
    ) {
        return stepBuilderFactory.get("purchaseStep")
                .transactionManager(transactionManager)
                .<Purchase, Purchase>chunk(50)
                .reader(purchaseItemReader)
                .writer(compositePurchaseItemWriter)
                .faultTolerant()
                .skipPolicy(new PurchaseSkipPolicy())
                .build();
    }

    @Bean("purchaseJob")
    public Job purchaseJob(@Qualifier("purchaseStep") Step step) {
        return jobBuilderFactory.get("purchaseJob")
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .build();
    }
}
