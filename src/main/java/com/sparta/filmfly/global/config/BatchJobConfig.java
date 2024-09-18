package com.sparta.filmfly.global.config;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@AllArgsConstructor
@EnableBatchProcessing
public class BatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Bean
    public Job movieCrawlingJob(Step movieCrawlingStep, JobExecutionListener jobExecutionListener) {
        return new JobBuilder("movieCrawlingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListener)
                .start(movieCrawlingStep)
                .build();
    }
    @Bean
    public Step movieCrawlingStep(Tasklet movieCrawlingTasklet) {
        return new StepBuilder("movieCrawlingStep", jobRepository)
                .tasklet(movieCrawlingTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job movieSearchJob(Step movieSearchStep, JobExecutionListener jobExecutionListener) {
        return new JobBuilder("movieSearchJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListener)
                .start(movieSearchStep)
                .build();
    }
    @Bean
    public Step movieSearchStep(Tasklet movieSearchTasklet) {
        return new StepBuilder("movieSearchStep", jobRepository)
                .tasklet(movieSearchTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job hardDeleteJob(Step userHardDeleteStep,
                             Step blockHardDeleteStep,
                             Step reportHardDeleteStep,
                             Step boardHardDeleteStep,
                             Step commentHardDeleteStep,
                             Step collectionHardDeleteStep,
                             Step reviewHardDeleteStep,
                             Step couponHardDeleteStep,
                             JobExecutionListener jobExecutionListener) {
        return new JobBuilder("hardDeleteJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListener)
                .start(userHardDeleteStep)
                .next(blockHardDeleteStep)
                .next(reportHardDeleteStep)
                .next(boardHardDeleteStep)
                .next(commentHardDeleteStep)
                .next(collectionHardDeleteStep)
                .next(reviewHardDeleteStep)
                .next(couponHardDeleteStep)
                .build();
    }

    @Bean
    public Step userHardDeleteStep(Tasklet userHardDeleteTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("userHardDeleteStep", jobRepository)
                .tasklet(userHardDeleteTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step blockHardDeleteStep(Tasklet blockHardDeleteTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("blockHardDeleteStep", jobRepository)
                .tasklet(blockHardDeleteTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step reportHardDeleteStep(Tasklet reportHardDeleteTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("reportHardDeleteStep", jobRepository)
                .tasklet(reportHardDeleteTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step boardHardDeleteStep(Tasklet boardHardDeleteTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("boardHardDeleteStep", jobRepository)
                .tasklet(boardHardDeleteTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step commentHardDeleteStep(Tasklet commentHardDeleteTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("commentHardDeleteStep", jobRepository)
                .tasklet(commentHardDeleteTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step collectionHardDeleteStep(Tasklet collectionHardDeleteTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("collectionHardDeleteStep", jobRepository)
                .tasklet(collectionHardDeleteTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step reviewHardDeleteStep(Tasklet reviewHardDeleteTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("reviewHardDeleteStep", jobRepository)
                .tasklet(reviewHardDeleteTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step couponHardDeleteStep(Tasklet couponHardDeleteTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("couponHardDeleteStep", jobRepository)
                .tasklet(couponHardDeleteTasklet, transactionManager)
                .build();
    }


}