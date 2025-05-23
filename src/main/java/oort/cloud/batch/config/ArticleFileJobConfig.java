package oort.cloud.batch.config;

import lombok.extern.slf4j.Slf4j;
import oort.cloud.batch.domain.Article;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Configuration
@Slf4j
public class ArticleFileJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ArticleFileJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job articleFileJob(){
        return new JobBuilder("articleFileJob", jobRepository)
                .start(articleFileStep())
                .build();
    }


    @Bean
    public Step articleFileStep(){
        return new StepBuilder("articleFileStep", jobRepository)
                .<Article, Article>chunk(10, transactionManager)
                .reader(articleFileItemReader(null))
                .writer(articleFileStdoutItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Article> articleFileItemReader(
        @Value("#{jobParameters['inputFile']}") String inputFile){
        return new FlatFileItemReaderBuilder<Article>()
                .name("articleFileItemReader")
                .resource(new ClassPathResource(inputFile))
                .delimited()
                .delimiter(",")
                .names("title", "desc", "createdAt")
                .customEditors(Map.of(LocalDateTime.class, localDateTimeEditor()))
                .targetType(Article.class)
                .linesToSkip(0)
                .build();
    }

    private PropertyEditor localDateTimeEditor(){
        return new PropertyEditorSupport(){
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                setValue(LocalDateTime.parse(text, formatter));
            }
        };
    }

    @Bean
    public ArticleFileStdoutItemWriter articleFileStdoutItemWriter(){
        return new ArticleFileStdoutItemWriter();
    }

    public static class ArticleFileStdoutItemWriter implements ItemWriter<Article> {
        @Override
        public void write(Chunk<? extends Article> chunk) throws Exception {
            for (Article article : chunk) {
                log.info("Article : {}", article);
            }
        }
    }

}
