package com.sparta.filmfly.dummytest;

import com.sparta.filmfly.domain.report.entity.ReportTypeEnum;
import com.sparta.filmfly.global.util.FileUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

class RandomReportGeneratorTest {
    public static final int NUMBER_OF_REPORTS = 100; // 생성할 신고 수
    private static final int NUMBER_OF_USERS = RandomEntityUserAndBoardAndCommentTest.NUMBER_OF_USER_RECORDS; // 생성할 유저 수
    private static final int DAYS_BEFORE = RandomEntityUserAndBoardAndCommentTest.DAYS_BEFORE; // 기준 날짜로부터 몇 일 전

    private static final int NUMBER_OF_REVIEWS = RandomReviewGeneratorTest.NUMBER_OF_REVIEWS; // 리뷰 수
    private static final int NUMBER_OF_BOARDS = RandomEntityUserAndBoardAndCommentTest.NUMBER_OF_BOARD_RECORDS; // 보드 수
    private static final int NUMBER_OF_COMMENTS = RandomEntityUserAndBoardAndCommentTest.NUMBER_OF_COMMENT_RECORDS; // 댓글 수

    private static final Map<ReportTypeEnum, List<String>> REASONS_BY_TYPE = Map.of(
        ReportTypeEnum.BOARD, Arrays.asList(
            "게시물에 부적절한 내용이 포함되어 있습니다.",
            "게시물에서 욕설과 비방이 발견되었습니다.",
            "게시물의 정보가 허위로 보입니다."
        ),
        ReportTypeEnum.REVIEW, Arrays.asList(
            "리뷰의 내용이 과장되거나 사실과 다릅니다.",
            "리뷰가 불쾌감을 주며 정확하지 않습니다.",
            "리뷰의 일부 내용이 부적절합니다."
        ),
        ReportTypeEnum.COMMENT, Arrays.asList(
            "댓글에 욕설이나 비방이 포함되어 있습니다.",
            "댓글이 스팸 또는 광고로 보입니다.",
            "댓글의 내용이 부적절하여 불쾌감을 줍니다."
        )
    );

    @Test
    public void generateRandomReports() {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(DAYS_BEFORE); // 오늘 날짜 기준 DAYS_BEFORE 일 전
        long secondsBetween = ChronoUnit.SECONDS.between(startDate, now);

        // 유저 데이터 생성
        List<Long> userIds = new ArrayList<>();
        for (long i = 1; i <= NUMBER_OF_USERS; i++) {
            userIds.add(i);
        }

        // 신고 데이터 생성
        List<ReportData> reports = new ArrayList<>();
        generateReports(userIds, reports, random, startDate, secondsBetween);

        // 생성 날짜 기준으로 정렬
        reports.sort(Comparator.comparing(ReportData::getCreatedAt));

        // 결과 출력
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO report (reporter_id, reported_id, content, type_id, type, reason, created_at, updated_at) VALUES\n");
        for (int i = 0; i < reports.size(); i++) {
            ReportData report = reports.get(i);
            sb.append(String.format("(%d, %d, '%s', %d, '%s', '%s', '%s', '%s')",
                report.getReporterId(), report.getReportedId(), report.getContent(), report.getTypeId(), report.getType(), report.getReason(), report.getCreatedAt(), report.getUpdatedAt()));

            if (i < reports.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append(";");

        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            FileUtils.saveSqlToFile("reportData.sql", sb.toString());
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

    private void generateReports(List<Long> userIds, List<ReportData> reports, Random random,
        LocalDateTime startDate, long secondsBetween) {
        Map<String, Set<String>> reportMap = new HashMap<>(); // reporterId와 reportedId에 대한 신고 이유 추적

        while (reports.size() < NUMBER_OF_REPORTS) {
            Long reporterId = getRandomElement(userIds, random);
            Long reportedId = getRandomElement(userIds, random);

            if (!reporterId.equals(reportedId)) { // 자기 자신을 신고하지 않도록 체크
                String reportKey = reporterId + "_" + reportedId;
                reportMap.putIfAbsent(reportKey, new HashSet<>());

                // 새로운 신고 이유를 선택하고 중복 여부를 체크
                ReportTypeEnum type = getRandomElement(Arrays.asList(ReportTypeEnum.values()), random);
                String reason;
                do {
                    reason = getRandomElement(REASONS_BY_TYPE.get(type), random);
                } while (reportMap.get(reportKey).contains(reason)); // 같은 이유로 중복 신고 방지

                // 신고 이유 추가
                reportMap.get(reportKey).add(reason);

                String content = "원본 Content 내용";
                Long typeId = getTypeIdByType(type, random); // type에 맞는 ID 생성

                // 생성시간과 수정시간 설정
                LocalDateTime reportCreationDate = startDate.plusSeconds(random.nextInt((int) secondsBetween + 1));
                LocalDateTime reportUpdateDate = reportCreationDate.plusDays(random.nextInt(10)); // 수정시간은 생성시간 이후 0~10일 사이

                String formattedCreationDate = reportCreationDate.toString().replace("T", " ");
                String formattedUpdateDate = reportUpdateDate.toString().replace("T", " ");

                // ReportData 객체로 신고 기록을 저장
                reports.add(new ReportData(reporterId, reportedId, content, typeId, type, reason, formattedCreationDate, formattedUpdateDate));
            }
        }
    }

    private <T> T getRandomElement(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    private Long getTypeIdByType(ReportTypeEnum type, Random random) {
        return switch (type) {
            case BOARD -> (long) (random.nextInt(NUMBER_OF_BOARDS) + 1);
            case REVIEW -> (long) (random.nextInt(NUMBER_OF_REVIEWS) + 1);
            case COMMENT -> (long) (random.nextInt(NUMBER_OF_COMMENTS) + 1);
        };
    }

    // 신고 데이터를 담기 위한 내부 클래스
    static class ReportData {
        private final long reporterId;
        private final long reportedId;
        private final String content;
        private final long typeId;
        private final ReportTypeEnum type;
        private final String reason;
        private final String createdAt;
        private final String updatedAt;

        public ReportData(long reporterId, long reportedId, String content, long typeId, ReportTypeEnum type, String reason, String createdAt, String updatedAt) {
            this.reporterId = reporterId;
            this.reportedId = reportedId;
            this.content = content;
            this.typeId = typeId;
            this.type = type;
            this.reason = reason;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public long getReporterId() {
            return reporterId;
        }

        public long getReportedId() {
            return reportedId;
        }

        public String getContent() {
            return content;
        }

        public long getTypeId() {
            return typeId;
        }

        public ReportTypeEnum getType() {
            return type;
        }

        public String getReason() {
            return reason;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}