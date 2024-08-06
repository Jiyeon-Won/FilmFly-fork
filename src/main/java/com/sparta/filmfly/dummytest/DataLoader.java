package com.sparta.filmfly.dummytest;

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
//        executeSqlScript("dummy/userData.sql");
//        executeSqlScript("dummy/blockData.sql");
//        executeSqlScript("dummy/boardData.sql");
//        executeSqlScript("dummy/commentData.sql");
//        executeSqlScript("dummy/movieData.sql");
//        executeSqlScript("dummy/reviewData.sql");
//        executeSqlScript("dummy/favoriteData.sql");
//        executeSqlScript("dummy/collectionData.sql");
//        executeSqlScript("dummy/reactionData.sql");
//        executeSqlScript("dummy/reportData.sql");
//        executeSqlScript("dummy/creditData.sql");
//        executeSqlScript("dummy/movieCredit.sql");
//        executeSqlScript("dummy/genre.sql");
//        executeSqlScript("dummy/movieGenreIds.sql");
    }
//    사용자 비밀번호는 Test12345! 로 통일

    private void executeSqlScript(String filePath) throws Exception {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(
                false, // continueOnError
                false, // ignoreFailedDrops
                "UTF-8", // encoding
                new ClassPathResource(filePath)
        );
        try {
            resourceDatabasePopulator.execute(dataSource);
        } catch (Exception e) {
            System.err.println("Error executing script: " + filePath);
            e.printStackTrace();
        }
    }
}