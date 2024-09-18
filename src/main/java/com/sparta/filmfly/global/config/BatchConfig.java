package com.sparta.filmfly.global.config;

import com.sparta.filmfly.domain.block.repository.BlockRepository;
import com.sparta.filmfly.domain.board.repository.BoardRepository;
import com.sparta.filmfly.domain.collection.repository.CollectionRepository;
import com.sparta.filmfly.domain.comment.repository.CommentRepository;
import com.sparta.filmfly.domain.coupon.repository.CouponRepository;
import com.sparta.filmfly.domain.report.repository.ReportRepository;
import com.sparta.filmfly.domain.review.repository.ReviewRepository;
import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.common.batch.JobCompletionListener;
import com.sparta.filmfly.global.common.batch.hardDelete.HardDeleteTasklet;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfig {

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobCompletionListener();
    }

    @Bean
    public Tasklet userHardDeleteTasklet(UserRepository userRepository) {
        return new HardDeleteTasklet<>(userRepository);
    }

    @Bean
    public Tasklet blockHardDeleteTasklet(BlockRepository blockRepository) {
        return new HardDeleteTasklet<>(blockRepository);
    }

    @Bean
    public Tasklet reportHardDeleteTasklet(ReportRepository reportRepository) {
        return new HardDeleteTasklet<>(reportRepository);
    }

    @Bean
    public Tasklet boardHardDeleteTasklet(BoardRepository boardRepository) {
        return new HardDeleteTasklet<>(boardRepository);
    }

    @Bean
    public Tasklet commentHardDeleteTasklet(CommentRepository commentRepository) {
        return new HardDeleteTasklet<>(commentRepository);
    }

    @Bean
    public Tasklet collectionHardDeleteTasklet(CollectionRepository collectionRepository) {
        return new HardDeleteTasklet<>(collectionRepository);
    }

    @Bean
    public Tasklet reviewHardDeleteTasklet(ReviewRepository reviewRepository) {
        return new HardDeleteTasklet<>(reviewRepository);
    }

    @Bean
    public Tasklet couponHardDeleteTasklet(CouponRepository couponRepository) {
        return new HardDeleteTasklet<>(couponRepository);
    }

}