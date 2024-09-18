package com.sparta.filmfly.dummytest;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        // 배치로 실행할 SQL 파일 리스트
//        List<String> sqlScripts = Arrays.asList(
//            "dummy/userData.sql",
//            "dummy/blockData.sql",
//            "dummy/boardData.sql",
//            "dummy/commentData.sql",
//            "dummy/movieData.sql",
//            "dummy/reviewData.sql",
//            "dummy/favoriteData.sql",
//            "dummy/collectionData.sql",
//            "dummy/reactionData.sql",
//            "dummy/reportData.sql",
//            "dummy/creditData.sql",
//            "dummy/movieCredit.sql",
//            "dummy/genre.sql",
//            "dummy/movieGenreIds.sql"
//        );
//
//        // SQL 파일들을 순차적으로 실행
//        for (String script : sqlScripts) {
//            try {
//                executeSqlScript(script);
//                System.out.println("Successfully executed script: " + script);
//            } catch (Exception e) {
//                System.err.println("Error executing script: " + script);
//                e.printStackTrace();
//            }
//        }
//        System.out.println("배치 종료");
    }

    private void executeSqlScript(String filePath) throws Exception {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(
            false, // continueOnError: true로 설정 시, 스크립트 실행 중 오류가 발생해도 계속 진행됨
            false, // ignoreFailedDrops: true로 설정 시, 테이블이 존재하지 않더라도 DROP 명령을 무시하고 계속 진행
            "UTF-8", // encoding: SQL 파일의 인코딩 방식
            new ClassPathResource(filePath)
        );
        resourceDatabasePopulator.execute(dataSource);
    }
}