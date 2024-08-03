package com.sparta.filmfly.global.config;

import com.sparta.filmfly.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;



@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final UserService userService;

    @Autowired
    public SchedulerConfig(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void deleteOldSoftDeletedUsers() {
        userService.deleteOldSoftDeletedUsers();
    }
}