package com.sparta.filmfly.dummytest;

import com.sparta.filmfly.global.util.FileUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

class RandomCollectionGeneratorTest {
    private static final List<Long> MOVIE_IDS = RandomReviewGeneratorTest.MOVIE_IDS;
    public static final int NUMBER_OF_COLLECTIONS = 4000; // 생성할 컬렉션 수
    private static final int MIN_MOVIES_PER_COLLECTION = 0; // 컬렉션당 최소 영화 수
    private static final int MAX_MOVIES_PER_COLLECTION = 30; // 컬렉션당 최대 영화 수
    private static final int NUMBER_OF_USERS = RandomEntityUserAndBoardAndCommentTest.NUMBER_OF_USER_RECORDS; // 생성할 유저 수
    private static final int DAYS_BEFORE = RandomEntityUserAndBoardAndCommentTest.DAYS_BEFORE; // 기준 날짜로부터 몇 일 전

    private static final List<String> COLLECTION_NAMES = Arrays.asList(
        "나의 영화 보관함", "최애 영화 리스트", "볼만한 영화", "추천 영화", "명작 모음",
        "기억에 남는 영화", "다시 보고 싶은 영화", "친구에게 추천할 영화", "특별한 영화", "영화 컬렉션",
        "액션 영화 베스트", "로맨스 영화 필름", "SF & 판타지 추천", "가족과 함께 보기 좋은 영화",
        "드라마 & 감동 영화", "최근 개봉작", "히어로 영화 모음", "클래식 영화", "인디 영화 추천",
        "애니메이션 필름"
    );

    private static final List<String> COLLECTION_CONTENTS = Arrays.asList(
        "이 보관함에는 내가 좋아하는 영화들이 모여 있어요.",
        "다시 보고 싶은 영화들을 모아둔 리스트입니다.",
        "친구들에게 추천할 만한 영화 모음입니다.",
        "감동적인 영화들만 모아 놓은 보관함입니다.",
        "한 번쯤 꼭 봐야 할 영화들입니다.",
        "액션 장르에서 최고로 평가받는 영화들을 모아봤습니다. 스릴과 박진감 넘치는 액션을 즐기세요!",
        "로맨스 장르에서 감동적이고 아름다운 영화들을 모았습니다. 사랑을 느껴보세요.",
        "SF와 판타지 장르의 환상적인 영화들을 추천합니다. 상상력과 모험이 가득합니다.",
        "가족과 함께 즐기기 좋은 영화들을 모아봤습니다. 모두가 함께 즐길 수 있는 영화들입니다.",
        "드라마와 감동적인 이야기로 가득한 영화들을 모았습니다. 진한 감동을 경험해보세요.",
        "최근에 개봉한 최신 영화를 모아봤습니다. 최신 트렌드를 놓치지 마세요.",
        "영웅들의 이야기를 담은 영화들을 모았습니다. 히어로와의 모험을 즐기세요.",
        "영화의 역사에서 중요한 클래식 영화들을 모았습니다. 시대를 초월한 명작을 감상하세요.",
        "독립 영화에서 찾아볼 수 있는 독특하고 신선한 영화들을 추천합니다. 새로운 경험을 해보세요.",
        "애니메이션의 매력적인 세계를 탐험하는 영화들을 모았습니다. 색다른 재미와 감동을 느껴보세요."
    );

    @Test
    public void generateRandomCollections() {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(DAYS_BEFORE); // 오늘 날짜 기준 DAYS_BEFORE 일 전
        long secondsBetween = ChronoUnit.SECONDS.between(startDate, now);

        // 유저 데이터 생성
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= NUMBER_OF_USERS; i++) {
            userIds.add(i);
        }

        // 컬렉션 데이터 생성
        List<CollectionData> collections = new ArrayList<>();
        List<MovieCollectionData> movieCollections = new ArrayList<>();
        generateCollections(userIds, collections, movieCollections, random, startDate, secondsBetween);

        // 생성 날짜 기준으로 정렬
        collections.sort(Comparator.comparing(CollectionData::getCreatedAt));
        movieCollections.sort(Comparator.comparing(MovieCollectionData::getCreatedAt));

        // 결과 출력
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO collection (user_id, name, content, created_at, updated_at) VALUES\n");
        for (int i = 0; i < collections.size(); i++) {
            CollectionData collection = collections.get(i);
            sb.append(String.format("(%d, '%s', '%s', '%s', '%s')",
                collection.getUserId(), collection.getName(), collection.getContent(), collection.getCreatedAt(), collection.getUpdatedAt()));

            if (i < collections.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append(";");

        sb.append("\n\n\n\nINSERT INTO movie_collection (collection_id, movie_id, created_at, updated_at) VALUES\n");
        for (int i = 0; i < movieCollections.size(); i++) {
            MovieCollectionData movieCollection = movieCollections.get(i);
            sb.append(String.format("(%d, %d, '%s', '%s')",
                movieCollection.getCollectionId(), movieCollection.getMovieId(), movieCollection.getCreatedAt(), movieCollection.getUpdatedAt()));

            if (i < movieCollections.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append(";");

        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            FileUtils.saveSqlToFile("collectionData.sql", sb.toString());
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

    private void generateCollections(List<Long> userIds,
        List<CollectionData> collections, List<MovieCollectionData> movieCollections, Random random, LocalDateTime startDate, long secondsBetween) {
        // 사용자별로 이미 사용한 컬렉션 이름을 추적하는 맵
        Map<Long, Set<String>> userCollectionNames = new HashMap<>();
        for (Long userId : userIds) {
            userCollectionNames.put(userId, new HashSet<>()); // 각 사용자에 대한 컬렉션 이름 Set을 초기화
        }

        for (int i = 0; i < RandomCollectionGeneratorTest.NUMBER_OF_COLLECTIONS; i++) {
            Long userId = getRandomElement(userIds, random); // 랜덤한 사용자 선택
            Set<String> usedNames = userCollectionNames.get(userId); // 해당 사용자의 이미 사용된 컬렉션 이름

            // 컬렉션 이름이 중복되지 않도록 고유한 이름을 생성
            String collectionName = generateUniqueName(usedNames, random);
            usedNames.add(collectionName);  // 사용된 이름을 사용자별 Set에 추가

            String collectionContent = getRandomElement(COLLECTION_CONTENTS, random);

            // 생성시간과 수정시간 설정
            LocalDateTime collectionCreationDate = startDate.plusSeconds(random.nextInt((int) secondsBetween + 1));
            LocalDateTime collectionUpdateDate = collectionCreationDate.plusDays(random.nextInt(10)); // 수정시간은 생성시간 이후 0~10일 사이

            String formattedCreationDate = collectionCreationDate.toString().replace("T", " ");
            String formattedUpdateDate = collectionUpdateDate.toString().replace("T", " ");

            // 컬렉션 데이터를 저장
            collections.add(new CollectionData(userId, collectionName, collectionContent, formattedCreationDate, formattedUpdateDate));

            long collectionId = collections.size(); // 임시로 컬렉션 ID 할당

            // 영화 컬렉션 데이터 생성
            int moviesPerCollection = Math.min(random.nextInt(MAX_MOVIES_PER_COLLECTION - MIN_MOVIES_PER_COLLECTION + 1) + MIN_MOVIES_PER_COLLECTION, RandomCollectionGeneratorTest.MOVIE_IDS.size());
            Set<Long> uniqueMovies = new HashSet<>();
            while (uniqueMovies.size() < moviesPerCollection) {
                Long movieId = getRandomElement(RandomCollectionGeneratorTest.MOVIE_IDS, random);
                uniqueMovies.add(movieId);

                // 생성시간과 수정시간 설정
                LocalDateTime movieCollectionCreationDate = startDate.plusSeconds(random.nextInt((int) secondsBetween + 1));
                LocalDateTime movieCollectionUpdateDate = movieCollectionCreationDate.plusDays(random.nextInt(10)); // 수정시간은 생성시간 이후 0~10일 사이

                String formattedMovieCollectionCreationDate = movieCollectionCreationDate.toString().replace("T", " ");
                String formattedMovieCollectionUpdateDate = movieCollectionUpdateDate.toString().replace("T", " ");

                // 영화 컬렉션 데이터를 저장
                movieCollections.add(new MovieCollectionData(collectionId, movieId, formattedMovieCollectionCreationDate, formattedMovieCollectionUpdateDate));
            }
        }
    }

    // 사용자별로 고유한 컬렉션 이름을 생성하는 메소드
    private String generateUniqueName(Set<String> usedNames, Random random) {
        String baseName = getRandomElement(COLLECTION_NAMES, random); // 기본 이름을 랜덤하게 선택
        String uniqueName = baseName;
        int counter = 1;

        // 사용된 이름이 있는지 확인하고, 중복될 경우 이름 뒤에 번호를 붙여서 고유하게 만듦
        while (usedNames.contains(uniqueName)) {
            uniqueName = baseName + " " + counter++;
        }

        return uniqueName; // 중복되지 않는 고유한 이름 반환
    }

    private <T> T getRandomElement(List<T> list, Random random) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List is empty, cannot get a random element.");
        }
        return list.get(random.nextInt(list.size()));
    }

    // 컬렉션 데이터를 담기 위한 내부 클래스
    static class CollectionData {
        private final long userId;
        private final String name;
        private final String content;
        private final String createdAt;
        private final String updatedAt;

        public CollectionData(long userId, String name, String content, String createdAt, String updatedAt) {
            this.userId = userId;
            this.name = name;
            this.content = content;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public long getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getContent() {
            return content;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }

    // 영화 컬렉션 데이터를 담기 위한 내부 클래스
    static class MovieCollectionData {
        private final long collectionId;
        private final long movieId;
        private final String createdAt;
        private final String updatedAt;

        public MovieCollectionData(long collectionId, long movieId, String createdAt, String updatedAt) {
            this.collectionId = collectionId;
            this.movieId = movieId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public long getCollectionId() {
            return collectionId;
        }

        public long getMovieId() {
            return movieId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}