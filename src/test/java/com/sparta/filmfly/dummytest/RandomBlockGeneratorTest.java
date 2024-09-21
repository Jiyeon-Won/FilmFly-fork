package com.sparta.filmfly.dummytest;

import com.sparta.filmfly.global.util.FileUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class RandomBlockGeneratorTest {
    public static final int NUMBER_OF_BLOCKS = 3000; // 생성할 차단 수
    private static final int NUMBER_OF_USERS = RandomEntityUserAndBoardAndCommentTest.NUMBER_OF_USER_RECORDS; // 생성할 유저 수
    private static final int DAYS_BEFORE = RandomEntityUserAndBoardAndCommentTest.DAYS_BEFORE; // 기준 날짜로부터 몇 일 전

    private static final List<String> MEMOS = Arrays.asList(
        "스팸 메시지 전송",
        "불쾌한 언행",
        "비매너 행동",
        "거래 사기",
        "욕설 사용",
        "광고성 메시지",
        "허위 정보 제공"
    );

    @Test
    public void generateRandomBlocks() {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(DAYS_BEFORE); // 오늘 날짜 기준 DAYS_BEFORE 일 전
        long secondsBetween = ChronoUnit.SECONDS.between(startDate, now);

        // 유저 데이터 생성
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= NUMBER_OF_USERS; i++) {
            userIds.add(i);
        }

        // 차단 데이터 생성
        List<BlockData> blocks = new ArrayList<>();
        generateBlocks(userIds, blocks, random, startDate, secondsBetween);

        // 생성 날짜 기준으로 정렬
        blocks.sort(Comparator.comparing(BlockData::getCreatedAt));

        // 결과 출력
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO block (blocker_id, blocked_id, memo, created_at, updated_at) VALUES\n");
        for (int i = 0; i < blocks.size(); i++) {
            BlockData block = blocks.get(i);
            sb.append(String.format("(%d, %d, '%s', '%s', '%s')",
                block.getBlockerId(), block.getBlockedId(), block.getMemo(), block.getCreatedAt(), block.getUpdatedAt()));

            if (i < blocks.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append(";");

        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            FileUtils.saveSqlToFile("blockData.sql", sb.toString());
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

    private void generateBlocks(List<Long> userIds, List<BlockData> blocks, Random random,
        LocalDateTime startDate, long secondsBetween) {
        Set<String> uniqueBlocks = new HashSet<>();
        while (blocks.size() < NUMBER_OF_BLOCKS) {
            Long blockerId = getRandomElement(userIds, random);
            Long blockedId = getRandomElement(userIds, random);

            if (!blockerId.equals(blockedId)) { // 자기 자신을 차단하지 않도록 체크
                String blockKey = String.format("%d_%d", blockerId, blockedId);
                if (uniqueBlocks.add(blockKey)) { // 중복 체크 후 추가
                    String memo = MEMOS.get(random.nextInt(MEMOS.size()));

                    // 생성시간과 수정시간 설정
                    LocalDateTime blockCreationDate = startDate.plusSeconds(random.nextInt((int) secondsBetween + 1));
                    LocalDateTime blockUpdateDate = blockCreationDate.plusDays(random.nextInt(10)); // 수정시간은 생성시간 이후 0~10일 사이

                    String formattedCreationDate = blockCreationDate.toString().replace("T", " ");
                    String formattedUpdateDate = blockUpdateDate.toString().replace("T", " ");

                    blocks.add(new BlockData(blockerId, blockedId, memo, formattedCreationDate, formattedUpdateDate));
                }
            }
        }
    }

    private <T> T getRandomElement(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    // Block 데이터를 담기 위한 내부 클래스
    static class BlockData {
        private final long blockerId;
        private final long blockedId;
        private final String memo;
        private final String createdAt;
        private final String updatedAt;

        public BlockData(long blockerId, long blockedId, String memo, String createdAt, String updatedAt) {
            this.blockerId = blockerId;
            this.blockedId = blockedId;
            this.memo = memo;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public long getBlockerId() {
            return blockerId;
        }

        public long getBlockedId() {
            return blockedId;
        }

        public String getMemo() {
            return memo;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}