package com.sparta.filmfly.dummytest;

import com.sparta.filmfly.global.util.FileUtils;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

class RandomEntityUserAndBoardAndCommentTest {
    private static final String[] FIXED_NICKNAMES = {
            "원지연", "백원하", "이은규", "한호진", "강준모"
    };
    private static final String FIXED_PASSWORD = "$2a$12$rQpJST/20h27oYcjOZ20XOqusfj5O.x2u9W1nnZ9RYdZWYU3IQwxu";
    public static final int NUMBER_OF_USER_RECORDS = 505; // 생성할 유저 레코드 수
    public static final int NUMBER_OF_BOARD_RECORDS = 20000; // 생성할 보드 레코드 수
    public static final int NUMBER_OF_COMMENT_RECORDS = 100000; // 생성할 댓글 레코드 수
    public static final int DAYS_BEFORE = 365; // 기준 날짜로부터 몇 일 전

    @Test
    public void testDataGeneration() {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(DAYS_BEFORE); // 오늘 날짜 기준 DAYS_BEFORE 일 전
        long secondsBetween = ChronoUnit.SECONDS.between(startDate, now); // startDate와 now 사이의 초 차이

        // 유저 데이터 생성
        Set<String> usedNicknames = new HashSet<>();
        StringBuilder userSb = new StringBuilder();

        for (int i = 1; i <= NUMBER_OF_USER_RECORDS; i++) {
            String nickname;
            String username;
            String role;
            if (i <= 5) {
                // admin 계정 설정
                username = "admin" + i;
                nickname = FIXED_NICKNAMES[i - 1];
                role = "ROLE_ADMIN";
            } else {
                username = "username" + (i - 5);
                nickname = (i <= FIXED_NICKNAMES.length)
                    ? FIXED_NICKNAMES[i - 1]
                    : generateUniqueKoreanName(random, usedNicknames);
                role = "ROLE_USER";
            }
            usedNicknames.add(nickname);

            String formattedDateTime = now.minusDays(DAYS_BEFORE + 1).toString().replace("T", " ");
            userSb.append(String.format(
                "('%s', '%s', 'user%d@example.com', '%s', NULL, NULL, 'ACTIVE', '%s', '%s', '%s')",
                username, FIXED_PASSWORD, i, nickname, role, formattedDateTime, formattedDateTime
            ));

            if (i < NUMBER_OF_USER_RECORDS) {
                userSb.append(",\n");
            }
        }

        // 보드 데이터 생성
        List<BoardData> boardDataList = new ArrayList<>();
        Map<Long, Set<String>> userBoardTitles = new HashMap<>(); // 사용자별로 게시글 제목을 저장할 Map

        for (int i = 1; i <= NUMBER_OF_BOARD_RECORDS; i++) {
            int userIndex = random.nextInt(NUMBER_OF_USER_RECORDS);
            long userId = userIndex + 1; // 유저 ID는 1부터 시작

            // 보드 생성일자: 유저 생성일자 이후의 랜덤한 시간
            long randomSeconds = random.nextInt((int) secondsBetween + 1);
            LocalDateTime boardCreationDate = startDate.plusSeconds(randomSeconds);

            String title = getRandomBoardTitle(random);
            String content = getRandomBoardContent(random);

            // 사용자별 제목 중복 확인
            Set<String> userTitles = userBoardTitles.computeIfAbsent(userId, k -> new HashSet<>());
            while (userTitles.contains(title)) {
                title = getRandomBoardTitle(random); // 중복이면 새로운 제목을 생성
            }
            userTitles.add(title); // 제목 추가

            String formattedDateTime = boardCreationDate.toString().replace("T", " ");
            long randomHits = random.nextInt(30); // 0부터 30까지의 랜덤 숫자

            boardDataList.add(new BoardData(userId, i, title, content, formattedDateTime, formattedDateTime, randomHits));
        }
        // 보드 데이터를 생성일자 기준으로 정렬
        boardDataList.sort(Comparator.comparing(BoardData::getCreatedAt));

        // 댓글 데이터 생성
        List<CommentData> commentDataList = new ArrayList<>();
        Set<String> existingComments = new HashSet<>();  // user_id와 board_id 조합을 저장할 Set
        int lastBoardCount = 100; // lastCommentNum개의 댓글을 작성할 게시물 개수
        int lastCommentNum = 125; // 한 게시물에 달릴 댓글의 개수

        // 기본 댓글 생성
        for (int i = 1; i <= NUMBER_OF_COMMENT_RECORDS - (lastBoardCount * lastCommentNum); i++) {
            long userId;
            long boardId;
            String commentKey;

            do {
                int boardIndex = random.nextInt(NUMBER_OF_BOARD_RECORDS);
                BoardData boardData = boardDataList.get(boardIndex);

                int userIndex = random.nextInt(NUMBER_OF_USER_RECORDS);
                userId = userIndex + 1;
                boardId = boardData.getBoardId();

                commentKey = userId + "_" + boardId;
            } while (existingComments.contains(commentKey));

            existingComments.add(commentKey);

            long randomSeconds = random.nextInt((int) secondsBetween + 1);
            LocalDateTime commentCreationDate = startDate.plusSeconds(randomSeconds);

            String formattedDateTime = commentCreationDate.toString().replace("T", " ");
            String commentContent = getRandomCommentContent(random);

            commentDataList.add(new CommentData(userId, boardId, commentContent, formattedDateTime));
        }

        // boardDataList의 뒤에서 100개의 게시글에 대해 100개 이상의 댓글 추가
        int last100StartIndex = boardDataList.size() - lastBoardCount; // 뒤에서 100개의 게시글을 선택
        for (int i = last100StartIndex; i < boardDataList.size(); i++) {
            BoardData boardData = boardDataList.get(i);
            long boardId = boardData.getBoardId();

            // 각 게시글에 대해 100개의 댓글 추가
            for (int j = 0; j < lastCommentNum; j++) {
                long userId;
                String commentKey;

                do {
                    int userIndex = random.nextInt(NUMBER_OF_USER_RECORDS);
                    userId = userIndex + 1;

                    commentKey = userId + "_" + boardId;
                } while (existingComments.contains(commentKey));

                existingComments.add(commentKey);

                long randomSeconds = random.nextInt((int) secondsBetween + 1);
                LocalDateTime commentCreationDate = startDate.plusSeconds(randomSeconds);

                String formattedDateTime = commentCreationDate.toString().replace("T", " ");
                String commentContent = getRandomCommentContent(random);

                commentDataList.add(new CommentData(userId, boardId, commentContent, formattedDateTime));
            }
        }

        // 댓글 데이터를 생성일자 기준으로 정렬
        commentDataList.sort(Comparator.comparing(CommentData::getCreatedAt));

        // 결과 출력
        userSb
            .insert(0, "INSERT INTO user (username, password, email, nickname, kakao_id, picture_url, user_status, user_role, created_at, updated_at) VALUES\n")
            .append(";");

        StringBuilder boardSb = new StringBuilder();
        for (BoardData boardData : boardDataList) {
            boardSb.append(String.format(
                "(%d, '%s', '%s', '%s', '%s', %d),\n",
                boardData.getUserId(), boardData.getTitle(), boardData.getContent(),
                boardData.getCreatedAt(), boardData.getUpdatedAt(), boardData.getHits()
            ));
        }
        // 마지막 쉼표를 세미콜론으로 변경
        if (!boardSb.isEmpty()) {
            boardSb.setLength(boardSb.length() - 2); // 마지막 ",\n"을 제거
            boardSb.append(";"); // 세미콜론 추가
        }
        boardSb.insert(0, "INSERT INTO board (user_id, title, content, created_at, updated_at, hits) VALUES\n");

        StringBuilder commentSb = new StringBuilder();
        for (CommentData commentData : commentDataList) {
            commentSb.append(String.format(
                "(%d, %d, '%s', '%s', '%s'),\n",
                commentData.getUserId(), commentData.getBoardId(), commentData.getContent(),
                commentData.getCreatedAt(), commentData.getCreatedAt()
            ));
        }
        // 마지막 쉼표를 세미콜론으로 변경
        if (!commentSb.isEmpty()) {
            commentSb.setLength(commentSb.length() - 2); // 마지막 ",\n"을 제거
            commentSb.append(";"); // 세미콜론 추가
        }
        commentSb.insert(0, "INSERT INTO comment (user_id, board_id, content, created_at, updated_at) VALUES\n");

        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(() -> {
            FileUtils.saveSqlToFile("userData.sql", userSb.toString());
        });
        executorService.execute(() -> {
            FileUtils.saveSqlToFile("boardData.sql", boardSb.toString());
        });
        executorService.execute(() -> {
            FileUtils.saveSqlToFile("commentData.sql", commentSb.toString());
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


    private String generateUniqueKoreanName(Random random, Set<String> usedNicknames) {
        String nickname;
        do {
            nickname = generateRandomKoreanName(random);
        } while (usedNicknames.contains(nickname));
        return nickname;
    }

    private String generateRandomKoreanName(Random random) {
        String[] familyNames = {"김", "이", "박", "정", "최", "원", "강", "한", "백"};
        String[] givenNames = {"가", "나", "다", "라", "마", "바", "사", "아", "자", "차", "카", "타", "파", "하", "지", "연", "준", "모", "원", "하", "은", "규", "호", "진",
        "손", "흥", "민", "황", "희", "찬", "이", "강", "인", "김", "민", "재", "유", "재", "석", "김", "종", "국"};

        String familyName = familyNames[random.nextInt(familyNames.length)];
        String givenName = givenNames[random.nextInt(givenNames.length)] +
                givenNames[random.nextInt(givenNames.length)];

        return familyName + givenName;
    }

    private String getRandomBoardTitle(Random random) {
        String[] titles = {
                "꼭 봐야 할 최고의 SF 영화", "최고의 로맨틱 코미디 10편", "저평가된 스릴러 영화", "반드시 봐야 할 공포 영화",
                "모두가 봐야 할 고전 영화", "숨겨진 보석: 독립 영화", "성인을 위한 최고의 애니메이션 영화",
                "지난 10년간 최고의 액션 영화", "놓친 최고의 다큐멘터리", "환상적인 비주얼을 자랑하는 판타지 영화",
                "마음을 혼란스럽게 하는 영화", "탐험할 가치가 있는 최고의 외국 영화", "역대 최고의 슈퍼히어로 영화",
                "영감을 주는 스포츠 영화", "실화를 바탕으로 한 영화", "역사상 최고의 전쟁 영화",
                "최고의 사운드트랙을 가진 영화", "가족 모두가 즐길 수 있는 영화", "꼭 봐야 할 컬트 클래식",
                "놀라운 촬영 기법을 자랑하는 영화", "가슴 아픈 영화", "최고의 미스터리 및 서스펜스 영화",
                "여름에 어울리는 청량한 영화", "겨울에 보기 좋은 따뜻한 영화", "친구들과 함께 볼 영화 추천",
                "인생을 바꿀만한 감동적인 영화", "독특한 스토리라인을 가진 영화", "훌륭한 영화 속 반전",
                "배우의 명연기가 돋보이는 영화", "고전 영화의 매력", "리메이크된 영화 비교 분석",
                "영화 속 가장 인상 깊은 장면", "한 번에 몰아보기 좋은 영화 시리즈", "가장 기대되는 신작 영화",
                "감독의 색깔이 뚜렷한 영화", "영화 속 최고의 명대사", "국내에서 만든 훌륭한 영화",
                "해외에서 주목받는 한국 영화", "마음을 울리는 애니메이션 영화", "역사적 사건을 다룬 영화",
                "비주얼이 압도적인 블록버스터", "음악과 함께하는 영화 여행", "강력한 메시지를 담은 영화",
                "여성 감독이 만든 명작 영화", "평범하지 않은 러브 스토리", "시간을 뛰어넘는 영화",
                "대자연을 배경으로 한 영화", "가장 무서운 공포 영화 장면", "판타지 세계로의 초대",
                "영화 속 인상적인 코스튬", "가족이 함께 볼 수 있는 영화", "친구들과 떠나는 영화 여행",
                "아이들과 함께 볼 수 있는 영화", "SF 영화 속 미래의 모습", "미스터리와 스릴러의 조합",
                "마음이 따뜻해지는 영화", "오래 기억에 남는 영화 결말", "심리 스릴러의 묘미",
                "영화 속 영웅과 악당", "기대되는 애니메이션 신작", "실화를 바탕으로 한 감동적인 영화",
                "독특한 콘셉트를 가진 영화", "영화 속 최고의 자동차 추격 장면", "시대극의 매력을 담은 영화",
                "감독의 숨겨진 걸작", "영화 속 환상적인 로케이션", "감독의 첫 작품 리뷰",
                "최고의 팀워크를 보여주는 영화", "가장 재미있는 영화 속 트리비아", "감동적인 가족 영화 추천",
                "주제별로 보는 영화 추천", "영화 속 화려한 액션 씬", "마음을 사로잡는 영화 포스터",
                "가장 기억에 남는 영화 속 음악", "영화 속 완벽한 캐릭터 소개", "주목할 만한 신예 배우",
                "영화 속 독특한 연출 기법", "다시 보고 싶은 영화 속 명장면", "가장 재미있는 영화 속 패러디",
                "단편 영화의 매력", "오래 남는 영화 속 대사", "영화 속 최고의 동물 캐릭터",
                "영화 속의 꿈과 현실", "다큐멘터리 영화의 진실성", "재미있는 영화 속 미스터리 요소",
                "영화 속 여행지 탐방", "고전 영화와 현대 영화의 비교", "가장 현실적인 영화 속 이야기",
                "판타지와 현실의 경계를 넘나드는 영화", "가장 매력적인 악당 캐릭터", "영화 속 숨겨진 디테일 찾기",
                "가장 아름다운 영화 속 장면", "영화 속 최고의 요리 장면", "역사적 배경을 가진 영화",
                "미래 사회를 그린 디스토피아 영화", "청소년이 보면 좋은 영화 추천", "가장 감동적인 영화 속 연애 장면",
                "인생을 생각하게 만드는 영화", "영화 속 사회 문제를 다룬 이야기", "가장 독창적인 애니메이션",
                "이색적인 주제를 다룬 영화", "영화 속 아이들의 세계", "가장 흥미진진한 범죄 영화"
        };
        return titles[random.nextInt(titles.length)];
    }

    private String getRandomBoardContent(Random random) {
        String[] contents = {
                "이 영화는 미래에 대해 생각하게 만듭니다.", "모두가 꼭 봐야 할 고전 영화입니다.",
                "이 영화가 로맨스를 다루는 방식이 정말 좋습니다.", "이 영화의 비주얼은 정말 놀랍습니다.",
                "스릴러 팬이라면 반드시 봐야 할 영화입니다!", "훌륭한 캐릭터 발전과 반전이 있는 영화입니다.",
                "이 영화의 사운드트랙은 정말 훌륭합니다.", "지금까지 본 최고의 액션 장면 중 하나입니다!",
                "깊고 감동적인 이야기로 공감을 자아냅니다.", "이 영화는 모든 면에서 걸작입니다.",
                "마음을 혼란스럽게 하는 플롯을 좋아한다면 이 영화는 꼭 봐야 합니다.", "이 영화는 인간의 감정을 탐구하는 훌륭한 영화입니다.",
                "영감을 주는 이야기가 담긴 아름다운 영화입니다.", "이 영화는 본 후에도 오랫동안 여운이 남습니다.",
                "이 영화의 촬영 기법은 최고입니다.", "더 많은 사람들이 봐야 할 저평가된 영화입니다.",
                "매우 감정적이고 몰입감 있는 이야기입니다.", "이 영화에서의 연기는 정말 뛰어납니다.",
                "처음부터 끝까지 놀라운 여정입니다.", "이 장르를 정의하는 진정한 고전입니다."
        };
        return contents[random.nextInt(contents.length)];
    }

    private String getRandomCommentContent(Random random) {
        String[] comments = {
                "이 영화에 대한 당신의 생각에 완전히 동의합니다.", "이 영화는 장르의 판도를 바꿨습니다.",
                "이 영화가 기대만큼 재미있진 않았어요.", "촬영 기법이 숨이 막히게 아름다웠습니다!",
                "주연 배우의 연기가 정말 강렬했습니다.", "이 영화는 내가 본 영화 중 최고의 플롯을 가지고 있습니다.",
                "사운드트랙이 정말 좋아요!", "결말이 너무 예상치 못했어요!", "정말 생각을 자극하는 영화였습니다.",
                "이 영화는 저를 울게 만들었어요.", "비주얼이 정말 놀라웠습니다.", "이 영화는 걸작이에요!",
                "모두가 꼭 봐야 할 영화입니다.", "영화를 보는 내내 긴장감이 넘쳤습니다.", "정말 잊을 수 없는 경험이었습니다.",
                "캐릭터 발전이 마음에 들어요.", "플롯 반전이 놀라웠습니다!", "정말 독특한 영화입니다.",
                "이 영화는 저를 말문이 막히게 했습니다.", "이 영화 생각을 멈출 수가 없어요.",
                "이 영화는 제 인생 영화입니다.", "정말 감동적인 이야기였어요.", "액션 장면이 압권이었어요.",
                "이 영화의 연출이 정말 인상 깊었어요.", "배경 음악이 영화와 완벽하게 어울렸어요.",
                "배우들의 케미가 최고였어요.", "스토리가 정말 탄탄했습니다.", "이 영화는 두 번 볼 가치가 있습니다.",
                "이 영화의 메시지가 마음에 남아요.", "이 영화 덕분에 많은 걸 배웠어요.", "감독의 연출력이 빛났어요.",
                "이 영화를 보고 나서 많은 생각이 들었어요.", "대사가 정말 인상적이었어요.",
                "영화 속 장면들이 너무 아름다웠습니다.", "이 영화는 진정한 예술 작품입니다.",
                "이 영화의 결말은 정말 예상치 못했어요.", "한 순간도 눈을 뗄 수 없었어요.",
                "이 영화는 오래 기억에 남을 거예요.", "정말 감동적인 이야기였습니다.",
                "이 영화는 마치 꿈을 꾸는 듯한 느낌이었어요.", "현실을 반영한 스토리가 인상적이었어요.",
                "이 영화의 영상미가 최고였습니다.", "정말 강렬한 인상을 남기는 영화예요.",
                "이 영화의 캐릭터들이 정말 매력적이었어요.", "스토리가 너무나도 독창적이었어요.",
                "이 영화는 시간이 어떻게 지나갔는지 모를 정도로 빠져들었어요.",
                "이 영화의 설정이 정말 흥미로웠습니다.", "영화가 끝난 후에도 여운이 남았습니다.",
                "이 영화는 감정적으로 굉장히 몰입하게 만들었어요.", "정말 현실감 넘치는 이야기였어요.",
                "이 영화는 제게 큰 영감을 주었어요.", "배우들의 연기가 인상적이었어요.",
                "이 영화는 꼭 다시 보고 싶어요.", "정말 강렬한 첫인상이었습니다.",
                "영화가 끝난 후에도 한동안 생각이 떠나지 않았어요.", "이 영화는 정말 놀라운 경험이었어요.",
                "스토리가 너무나도 흥미진진했어요.", "이 영화는 정말로 독창적이었어요.",
                "이 영화의 분위기가 너무 마음에 들었어요.", "영화의 플롯이 아주 잘 짜여져 있었어요.",
                "이 영화의 대사 하나하나가 인상 깊었어요.", "주연 배우의 연기가 정말 최고였습니다.",
                "이 영화의 음악이 모든 것을 완벽하게 만들었어요.", "스토리텔링이 정말 훌륭했어요.",
                "이 영화의 촬영 기법이 너무 좋았습니다.", "이 영화는 시간이 흘러도 기억에 남을 거예요.",
                "정말 독특한 시각적 경험이었어요.", "이 영화는 영화의 경계를 넘는 작품입니다.",
                "이 영화는 제 기대를 완전히 뛰어넘었어요.", "이 영화는 정말 흥미로웠습니다.",
                "이 영화의 주제가 정말 깊이 와닿았습니다.", "정말 몰입감 넘치는 영화였어요.",
                "이 영화는 꼭 소장하고 싶어요.", "이 영화는 정말 특별했습니다.",
                "스토리가 너무나도 매력적이었어요.", "영화의 시작부터 끝까지 몰입했어요.",
                "이 영화의 캐릭터가 너무 마음에 들었어요.", "정말 독특한 플롯이었어요.",
                "이 영화는 제 기대를 완전히 충족시켰어요.", "이 영화의 영상미가 정말 뛰어났어요.",
                "이 영화는 내내 눈을 뗄 수 없게 만들었어요.", "이 영화의 결말이 너무 마음에 들었어요.",
                "이 영화의 캐릭터가 정말 매력적이었어요.", "정말 감동적인 스토리였어요.",
                "이 영화는 시간이 어떻게 흘렀는지 모를 정도로 재밌었어요.", "정말 독특한 경험이었어요.",
                "이 영화는 제게 큰 영감을 주었어요.", "이 영화는 시간이 지나도 기억에 남을 것 같아요.",
                "이 영화는 정말 기대 이상이었어요.", "정말 몰입감 있는 영화였습니다.",
                "이 영화는 시작부터 끝까지 흥미진진했어요.", "이 영화는 정말로 독특한 작품이었어요.",
                "이 영화의 분위기가 너무 좋았어요.", "스토리가 아주 잘 짜여져 있었어요.",
                "이 영화의 대사가 정말 인상 깊었어요.", "주연 배우의 연기가 정말 뛰어났습니다.",
                "이 영화의 음악이 정말 좋았어요.", "스토리텔링이 정말 훌륭했어요.",
                "이 영화의 촬영 기법이 정말 좋았어요.", "이 영화는 시간이 지나도 기억에 남을 거예요."
        };
        return comments[random.nextInt(comments.length)];
    }


    // 보드 데이터 클래스
    static class BoardData {
        private final long userId;
        private final long boardId;
        private final String title;
        private final String content;
        private final String createdAt;
        private final String updatedAt;
        private final long hits;

        public BoardData(long userId, long boardId, String title, String content, String createdAt, String updatedAt, long hits) {
            this.userId = userId;
            this.boardId = boardId;
            this.title = title;
            this.content = content;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.hits = hits;
        }

        public long getUserId() {
            return userId;
        }

        public long getBoardId() {
            return boardId;
        }

        public String getTitle() {
            return title;
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

        public long getHits() {
            return hits;
        }
    }

    // 댓글 데이터를 담기 위한 내부 클래스
    static class CommentData {
        private final long userId;
        private final long boardId;
        private final String content;
        private final String createdAt;

        public CommentData(long userId, long boardId, String content, String createdAt) {
            this.userId = userId;
            this.boardId = boardId;
            this.content = content;
            this.createdAt = createdAt;
        }

        public long getUserId() {
            return userId;
        }

        public long getBoardId() {
            return boardId;
        }

        public String getContent() {
            return content;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}