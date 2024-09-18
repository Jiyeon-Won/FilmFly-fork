package com.sparta.filmfly.dummytest;

import com.sparta.filmfly.global.util.FileUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class RandomReviewGeneratorTest {

    public static List<Long> MOVIE_IDS = new ArrayList<>(); // 추출된 ID를 저장할 리스트
    public static final int NUMBER_OF_REVIEWS = 2300; // 생성할 리뷰 수
    private static final int NUMBER_OF_USERS = RandomEntityUserAndBoardAndCommentTest.NUMBER_OF_USER_RECORDS; // 생성할 유저 수
    private static final int DAYS_BEFORE = RandomEntityUserAndBoardAndCommentTest.DAYS_BEFORE; // 기준 날짜로부터 몇 일 전

    @Test
    public void generateRandomReviews() {
        // movieData.sql 파일에서 영화 ID 추출
        String filePath = "src/main/resources/dummy/" + "movieData.sql"; // movieData.sql 파일 경로
        MOVIE_IDS = extractMovieIds(filePath); // 영화 ID 추출

        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(DAYS_BEFORE); // 오늘 날짜 기준 DAYS_BEFORE 일 전
        long secondsBetween = ChronoUnit.SECONDS.between(startDate, now);

        // 유저 데이터 생성
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= NUMBER_OF_USERS; i++) {
            userIds.add(i);
        }

        // 리뷰 데이터 생성
        List<ReviewData> reviews = new ArrayList<>();
        generateReviews(userIds, reviews, random, startDate, secondsBetween);

        // 생성 날짜 기준으로 정렬
        reviews.sort(Comparator.comparing(ReviewData::getCreatedAt));

        // 결과 출력
        StringBuilder sb = new StringBuilder();
        sb.append(
            "INSERT INTO review (user_id, movie_id, title, content, rating, created_at, updated_at) VALUES\n");
        for (int i = 0; i < reviews.size(); i++) {
            ReviewData review = reviews.get(i);
            sb.append(String.format("(%d, %d, '%s', '%s', %.1f, '%s', '%s')",
                review.getUserId(), review.getMovieId(), review.getTitle(), review.getContent(),
                review.getRating(), review.getCreatedAt(), review.getUpdatedAt()));

            if (i < reviews.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append(";");

        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            FileUtils.saveSqlToFile("reviewData.sql", sb.toString());
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

    // movieData.sql에서 영화 ID 추출하는 메서드
    public static List<Long> extractMovieIds(String filePath) {
        Pattern pattern = Pattern.compile("INSERT INTO movie \\(id, .*?\\) VALUES \\((\\d+),"); // 영화 ID 추출하는 정규식
        List<Long> movieIds = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath), 16 * 1024)) {
            movieIds = br.lines()  // 스트림 방식으로 파일 읽기
                .map(pattern::matcher)  // 정규식 매칭
                .filter(Matcher::find)  // 매칭된 라인만 처리
                .map(matcher -> Long.parseLong(matcher.group(1)))  // ID 추출 및 변환
                .collect(Collectors.toList());  // 리스트로 수집
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieIds;
    }

    private void generateReviews(List<Long> userIds,
        List<ReviewData> reviews, Random random, LocalDateTime startDate, long secondsBetween) {

        Set<String> existingReviews = new HashSet<>();  // 중복된 리뷰 방지

        for (int i = 0; i < NUMBER_OF_REVIEWS; i++) {
            long userId;
            long movieId;
            String reviewKey;

            do {
                userId = getRandomElement(userIds, random);
                movieId = getRandomElement(MOVIE_IDS, random);
                reviewKey = userId + "_" + movieId;
            } while (existingReviews.contains(reviewKey));  // 중복된 조합이 존재하면 다시 시도

            existingReviews.add(reviewKey);

            float rating = 1 + random.nextInt(5); // 별점 1~5
            String title = generateReviewTitle(rating, random);
            String content = generateReviewContent(rating, random);

            LocalDateTime reviewCreationDate = startDate.plusSeconds(
                random.nextInt((int) secondsBetween + 1));
            LocalDateTime reviewUpdateDate = reviewCreationDate.plusDays(
                random.nextInt(10)); // 수정시간은 생성시간 이후

            String formattedCreationDate = reviewCreationDate.toString().replace("T", " ");
            String formattedUpdateDate = reviewUpdateDate.toString().replace("T", " ");

            reviews.add(
                new ReviewData(userId, movieId, title, content, rating, formattedCreationDate,
                    formattedUpdateDate));
        }
    }

    private String generateReviewTitle(float rating, Random random) {
        return switch ((int) rating) {
            case 5 -> getRandomElement(Arrays.asList("최고의 영화!", "이 영화는 꼭 봐야 해요!", "완벽한 영화 경험"), random);
            case 4 -> getRandomElement(Arrays.asList("좋은 영화입니다", "볼만한 영화", "즐겁게 볼 수 있는 영화"), random);
            case 3 -> getRandomElement(Arrays.asList("그냥 그렇습니다", "평범한 영화", "무난한 영화"), random);
            case 2 -> getRandomElement(Arrays.asList("실망스러운 영화", "별로 추천하지 않습니다", "아쉬운 영화"), random);
            case 1 -> getRandomElement(Arrays.asList("최악의 영화", "정말로 보기 싫었던 영화", "시간을 낭비했습니다"), random);
            default -> "리뷰 제목 없음";
        };
    }

    private String generateReviewContent(float rating, Random random) {
        return switch ((int) rating) {
            case 5 -> getRandomElement(Arrays.asList("이 영화는 정말 훌륭했습니다! 모든 면에서 완벽했어요.", "감동적인 스토리와 훌륭한 연기!"), random);
            case 4 -> getRandomElement(Arrays.asList("좋은 영화였습니다.", "전반적으로 괜찮은 영화였지만, 몇 가지 아쉬운 점이 있었습니다."), random);
            case 3 -> getRandomElement(Arrays.asList("보통 수준의 영화입니다.", "그저 그런 영화였어요."), random);
            case 2 -> getRandomElement(Arrays.asList("많이 실망스러웠습니다.", "기대 이하의 영화였습니다."), random);
            case 1 -> getRandomElement(Arrays.asList("정말 최악의 영화였습니다.", "이 영화는 정말 최악이었어요."), random);
            default -> "리뷰 내용 없음";
        };
    }

    private <T> T getRandomElement(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    // 리뷰 데이터를 담기 위한 내부 클래스
    static class ReviewData {

        private final long userId;
        private final long movieId;
        private final String title;
        private final String content;
        private final float rating;
        private final String createdAt;
        private final String updatedAt;

        public ReviewData(long userId, long movieId, String title, String content, float rating,
            String createdAt, String updatedAt) {
            this.userId = userId;
            this.movieId = movieId;
            this.title = title;
            this.content = content;
            this.rating = rating;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public long getUserId() {
            return userId;
        }

        public long getMovieId() {
            return movieId;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public float getRating() {
            return rating;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}