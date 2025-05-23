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

@Configuration
@Slf4j
public class JobParameterTest2Config {
    @Bean
    public Job jobParameterTest2Job(JobRepository jobRepository, Step jobParameterTestStep){
        return new JobBuilder("jobParameterTest2Job", jobRepository)
                .start(jobParameterTestStep)
                .build();
    }

    @Bean
    public Step jobParameterTest2Step(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     Tasklet jobParameterTest2Tasklet
                                     ){
        return new StepBuilder("jobParameterTest2Step", jobRepository)
                .tasklet(jobParameterTest2Tasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet jobParameterTest2Tasklet(
            @Value("#{jobParameters['parameterCount']}") String range
    ){
        return (contribution, chunkContext) -> {
            String[] countRange = range.split("-");
            int start = Integer.parseInt(countRange[0]);
            int end = Integer.parseInt(countRange[1]);
            log.info("JobParameter Count range : {} - {}", start, end);
            log.info("반복 카운트 : {}", end);
            for(int i = start; i <= end; i++){
                log.info("{} 번째 반복 작업 중...", i);
            }
            log.info("Job : {} 종료", "JobParameter Json Test");
            return RepeatStatus.FINISHED;
        };
    }
}
