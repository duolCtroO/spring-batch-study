package oort.cloud.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *  실행 커맨드
 *  ./gradlew bootRun --args='--spring.batch.job.name=jobParameterTestJob parameterTestId=1,java.lang.String parameterCount=5,java.lang.Integer'
 */
@Configuration
@Slf4j
public class JobParameterTestConfig {
    @Bean
    public Job jobParameterTestJob(JobRepository jobRepository, Step jobParameterTestStep){
        return new JobBuilder("jobParameterTestJob", jobRepository)
                .start(jobParameterTestStep)
                .build();
    }

    @Bean
    public Step jobParameterTestStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     Tasklet jobParameterTestTasklet
                                     ){
        return new StepBuilder("jobParameterTestStep", jobRepository)
                .tasklet(jobParameterTestTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet jobParameterTestTasklet(
            @Value("#{jobParameters['parameterTestId']}") String parameterTestId,
            @Value("#{jobParameters['parameterCount']}") Integer count
    ){
        return (contribution, chunkContext) -> {
            log.info("JobParameter Test ID : {}", parameterTestId);
            log.info("반복 카운트 : {}", count);
            for(int i = 1; i <= count; i++){
                log.info("{} 번째 반복 작업 중...", i);
            }
            log.info("Job : {} 종료", "JobParameterTest");
            return RepeatStatus.FINISHED;
        };
    }
}
