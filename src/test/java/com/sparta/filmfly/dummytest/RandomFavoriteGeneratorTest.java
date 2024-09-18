package com.sparta.filmfly.dummytest;

import com.sparta.filmfly.global.util.FileUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class RandomFavoriteGeneratorTest {
    private static final List<Long> MOVIE_IDS = RandomReviewGeneratorTest.MOVIE_IDS;
    public static final int NUMBER_OF_FAVORITES = 1000; // 생성할 찜하기 수
    private static final int NUMBER_OF_USERS = RandomEntityUserAndBoardAndCommentTest.NUMBER_OF_USER_RECORDS; // 생성할 유저 수

    @Test
    public void generateRandomFavorites() {
        Random random = new Random();

        // 유저 데이터 생성
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= NUMBER_OF_USERS; i++) {
            userIds.add(i);
        }

        // 찜하기 데이터 생성
        Set<String> favorites = new HashSet<>();
        generateFavorites(userIds, favorites, random);

        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            FileUtils.saveSqlToFile("favoriteData.sql",
                "INSERT INTO favorite (user_id, movie_id) VALUES\n"
                    + String.join(",\n", favorites)
                    + ";"
            );
        });
        // 스레드 풀 종료
        executorService.shutdown();
        try {
            // 모든 스레드가 작업을 완료할 때까지 대기
            if (executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                System.out.println("모든 작업이 완료되었습니다.");
            } else {
                System.out.println("일부 작업이 시간 내에 완료되지 않았습니다.");
            }
        } catch (InterruptedException e) {
            System.out.println("작업 중 인터럽트가 발생했습니다.");
        }
    }

    private void generateFavorites(List<Long> userIds, Set<String> favorites, Random random) {
        // Check if movieIds is empty before proceeding
        if (MOVIE_IDS.isEmpty()) {
            throw new IllegalArgumentException("The movieIds list is empty, cannot generate favorites.");
        }

        Map<Long, Set<Long>> userMovieMap = new HashMap<>(); // 유저별 찜한 영화 ID를 관리하는 맵

        while (favorites.size() < NUMBER_OF_FAVORITES) {
            Long userId = getRandomElement(userIds, random);
            Long movieId = getRandomElement(MOVIE_IDS, random);

            // 유저별로 찜한 영화 ID들을 관리하여 중복을 방지
            userMovieMap.putIfAbsent(userId, new HashSet<>());

            if (userMovieMap.get(userId).add(movieId)) { // 중복이 아니라면 추가
                String favoriteInsertKey = String.format("(%d, %d)", userId, movieId);
                favorites.add(favoriteInsertKey);
            }
        }
    }

    private <T> T getRandomElement(List<T> list, Random random) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List is empty, cannot get a random element.");
        }
        return list.get(random.nextInt(list.size()));
    }
}