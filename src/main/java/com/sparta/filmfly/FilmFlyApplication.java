package com.sparta.filmfly;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
public class FilmFlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmFlyApplication.class, args);

        long heapSize = Runtime.getRuntime().totalMemory();  // 현재 힙 메모리 사용량
        long heapMaxSize = Runtime.getRuntime().maxMemory(); // 힙 메모리 최대 크기
        long heapFreeSize = Runtime.getRuntime().freeMemory(); // 사용 가능한 힙 메모리

        System.out.println("현재 힙 메모리 사용량: " + (heapSize / (1024 * 1024)) + " MB");
        System.out.println("힙 메모리 최대 크기: " + (heapMaxSize / (1024 * 1024)) + " MB");
        System.out.println("사용 가능한 힙 메모리: " + (heapFreeSize / (1024 * 1024)) + " MB");
    }

    @PostConstruct
    void started(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        System.out.println("현재 타임존: " + TimeZone.getDefault().getID());
    }
}