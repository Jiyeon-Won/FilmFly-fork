package com.sparta.filmfly.dummytest;

import com.sparta.filmfly.global.util.FileUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

class RandomReactionGeneratorTest {
    private static final List<Long> MOVIE_IDS = RandomReviewGeneratorTest.MOVIE_IDS;
    private static final int NUMBER_OF_REVIEWS = RandomReviewGeneratorTest.NUMBER_OF_REVIEWS; // 리뷰 수
    private static final int NUMBER_OF_GOOD_REACTIONS = 5000; // 생성할 좋아요 리액션 수
    private static final int NUMBER_OF_BAD_REACTIONS = 2000; // 생성할 싫어요 리액션 수
    private static final int NUMBER_OF_USERS = RandomEntityUserAndBoardAndCommentTest.NUMBER_OF_USER_RECORDS; // 생성할 유저 수
    private static final int NUMBER_OF_BOARDS = RandomEntityUserAndBoardAndCommentTest.NUMBER_OF_BOARD_RECORDS; // 생성할 보드 수
    private static final int NUMBER_OF_COMMENTS = RandomEntityUserAndBoardAndCommentTest.NUMBER_OF_COMMENT_RECORDS; // 생성할 댓글 수

    @Test
    public void generateReactionData() {
        Random random = new Random();

        // 유저 데이터 생성
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= NUMBER_OF_USERS; i++) {
            userIds.add(i);
        }

        // 리액션 데이터 생성
        Set<String> goodReactions = new HashSet<>();
        Set<String> badReactions = new HashSet<>();
        ReactionContentTypeEnum[] types = ReactionContentTypeEnum.values();

        // 좋아요 리액션 생성
        generateReactions(NUMBER_OF_GOOD_REACTIONS, userIds, types, goodReactions, random);

        // 싫어요 리액션 생성
        generateReactions(NUMBER_OF_BAD_REACTIONS, userIds, types, badReactions, random);

        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            FileUtils.saveSqlToFile("reactionData.sql",
                "INSERT INTO good (user_id, type, type_id) VALUES\n"
                    + String.join(",\n", goodReactions) + ";\n\n\n\n"
                    + "INSERT INTO bad (user_id, type, type_id) VALUES\n"
                    + String.join(",\n", badReactions) + ";"
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

    private void generateReactions(int numberOfReactions, List<Long> userIds, ReactionContentTypeEnum[] types,
        Set<String> reactions, Random random) {
        Map<Long, Set<String>> userReactionMap = new HashMap<>();  // 사용자가 리액션한 콘텐츠를 추적하기 위한 맵

        while (reactions.size() < numberOfReactions) {
            Long userId = getRandomElement(userIds, random);
            ReactionContentTypeEnum type = getRandomElement(Arrays.asList(types), random);
            Long typeId = null;

            if (type == ReactionContentTypeEnum.MOVIE) {
                typeId = getRandomElement(MOVIE_IDS, random);
            } else if (type == ReactionContentTypeEnum.BOARD) {
                typeId = getRandomElement(getBoardIds(), random);
            } else if (type == ReactionContentTypeEnum.REVIEW) {
                typeId = getRandomElement(getReviewIds(), random);
            } else if (type == ReactionContentTypeEnum.COMMENT) {
                typeId = getRandomElement(getCommentIds(), random);
            }

            String reactionKey = String.format("%s_%d", type.getContentType(), typeId);

            // 사용자가 해당 콘텐츠에 대해 이미 리액션을 했는지 확인
            userReactionMap.putIfAbsent(userId, new HashSet<>());
            Set<String> userReactions = userReactionMap.get(userId);

            if (userReactions.add(reactionKey)) { // 중복이 아니라면 추가
                String reactionInsertKey = String.format("(%d, '%s', %d)", userId, type.getContentType(), typeId);
                reactions.add(reactionInsertKey);
            }
        }
    }

    private List<Long> getBoardIds() {
        List<Long> boardIds = new ArrayList<>();
        for (long i = 1; i <= NUMBER_OF_BOARDS; i++) {
            boardIds.add(i);
        }
        return boardIds;
    }

    private List<Long> getReviewIds() {
        List<Long> reviewIds = new ArrayList<>();
        for (long i = 1; i <= NUMBER_OF_REVIEWS; i++) {
            reviewIds.add(i);
        }
        return reviewIds;
    }

    private List<Long> getCommentIds() {
        List<Long> commentIds = new ArrayList<>();
        for (long i = 1; i <= NUMBER_OF_COMMENTS; i++) {
            commentIds.add(i);
        }
        return commentIds;
    }

    private <T> T getRandomElement(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    // ReactionContentTypeEnum 클래스
    private enum ReactionContentTypeEnum {
        MOVIE("movie"),
        REVIEW("review"),
        BOARD("board"),
        COMMENT("comment");

        private final String contentType;

        ReactionContentTypeEnum(String contentType) {
            this.contentType = contentType;
        }

        public String getContentType() {
            return contentType;
        }
    }
}