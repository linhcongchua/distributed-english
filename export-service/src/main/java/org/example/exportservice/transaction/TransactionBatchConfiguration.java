package org.example.exportservice.transaction;

import org.example.exportservice.Person;
import org.example.exportservice.PersonItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//@Configuration
public class TransactionBatchConfiguration {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    @Bean
    public JdbcPagingItemReader<Transaction> reader(DataSource dataSource, @Value("#{jobParameters['dae']}") String date, PagingQueryProvider queryProvider) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("status", "NEW");

        return new JdbcPagingItemReaderBuilder<Transaction>()
                .name("transaction_export_csv_reader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .parameterValues(parameters)
                .rowMapper(new TransactionRowMapper())
                .pageSize(1000)
                .build();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean queryProvider() {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean(); // todo: PostgresPagingQueryProvider

        provider.setSelectClause("select id, name, credit");
        provider.setFromClause("from transaction");
        provider.setWhereClause("where status = :status");
        provider.setSortKey("id desc");

        return provider;
    }

    @Bean
    public FlatFileItemWriter<Transaction> writer() {
        return new FlatFileItemWriterBuilder<Transaction>()
                .name("transaction_export_csv_writer")
                .resource(buildResource())
                .lineAggregator(delimitedLineAggregator())
                .build();

    }

    public FileSystemResource buildResource() {
        return new FileSystemResource("daily_transaction_export_"
                + DATE_FORMAT.format(new Date()) + ".csv");
    }

    @Bean
    public DelimitedLineAggregator<Transaction> delimitedLineAggregator() {
        final DelimitedLineAggregator<Transaction> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter(",");
        BeanWrapperFieldExtractor<Transaction> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"id", "name", "credit"});
        delimitedLineAggregator.setFieldExtractor(extractor);
        return delimitedLineAggregator;
    }

    @Bean TransactionProcessor processor() {
        return new TransactionProcessor();
    }

    @Bean
    public Step stepExport(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      JdbcPagingItemReader<Transaction> reader, TransactionProcessor processor, FlatFileItemWriter<Transaction> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Transaction, Transaction> chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job exportTransactionJob(JobRepository jobRepository, Step stepExport) {
        return null;
    }
}
