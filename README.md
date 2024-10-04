# 🎞️ FilmFly
 FilmFly는 영화 정보를 제공하고, 사용자가 리뷰를 남기고 소통할 수 있는 서비스입니다.


<br/><br/>
# 🗓️ 프로젝트 기간
### 2024.07.18 ~ 2024.08.16


<br/><br/>
# 🪪 팀원 / 역할분담
### 팀명 : Reviewers
<details>
    <summary>원지연 <b>(팀장)</b></summary>
    <ul>
        <li>리뷰, 좋아요, 싫어요</li>
        <li>프론트 전반적인 틀 작업</li>
        <li>CloudFront, S3 연결</li>
        <li>더미 데이터 제작 - credit, genre, movieCredit, movieGenrelds 등등.. </li>
    </ul>
</details>
<details>
    <summary>백원하 <b>(부팀장)</b></summary> 
    <ul>
        <li>영화, 보관함, 찜, 배우, 장</li>
        <li>TMDB API 를 활용해 관리자용 데이터 크롤링</li>
        <li>프로젝트 RDS 연동</li>
        <li>Github Actions, Docker, EC2 연동</li>
        <li>(프론트) - 메인 페이지 제작 및 백엔드 연동 및 추가 api 구현</li>
    </ul>
</details>
<details>
      <summary>이은규</summary>
      <ul>
        <li>시큐리티, 사용자 기능, 신고, 차단</li>
        <li>소셜 로그인</li>
        <li>이메일 인증</li>
        <li>(프론트) - 관리자 페이지 제작, UI 최종 디자인 수정</li>
      </ul>
</details>

<details>
        <summary>한호진</summary> 
        <ul>
            <li>운영보드, 쿠폰</li>
            <li>좋아요, 싫어요 코드 추가 기능 개발</li>
            <li>(프론트) - 유저가 갖고 있는 쿠폰, 마이페이지 작업</li>
        </ul>
</details>
<details>
        <summary>강준모</summary> 
        <ul>
            <li>게시판, 댓글</li>
            <li>썸머노트 연동 게시판 작업, S3 이미지 관리</li>
            <li>운영 게시판, 보관함 추가 기능 개발</li>
            <li>더미 데이터 제작 - 유저, 블락, 영화 보관함, 찜하기, 좋아요, 싫어요, 게시글, 댓글, 리뷰</li>
            <li>(프론트) - 유저의 게시글, 댓글, 리뷰, 찜, 보관함, 영화 페이지 보관함 기능</li>
        </ul>
</details>


<br/><br/>
# ⚙️ 기술 스택

| **분류**        | **기술**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|---------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **IDE**       | <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white">                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| **Framework** | <img src="https://img.shields.io/badge/springboot(3.3.1)-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **Language**  | <img src="https://img.shields.io/badge/java(JDK17)-007396?style=for-the-badge&logo=OpenJDK&logoColor=white">                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| **Database**  | <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"> <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white">                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| **Tools**     | <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white">                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| **Server**    | <img src="https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=Amazon%20EC2&logoColor=white"> <img src="https://img.shields.io/badge/Amazon%20S3-569A31?style=for-the-badge&logo=Amazon%20S3&logoColor=white"> <img src="https://img.shields.io/badge/Amazon%20RDS-527FFF.svg?style=for-the-badge&logo=Amazon-RDS&logoColor=white"> <img src="https://img.shields.io/badge/Amazon%20CloudFront-FF4F8B.svg?style=for-the-badge&logo=Amazon-CloudWatch&logoColor=white">  <br/> <img src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white"> <img src="https://img.shields.io/badge/ElastiCache-005571?style=for-the-badge&logo=Elasticsearch&logoColor=white"> <img src="https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white"> <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white"> |
| **Front**     | <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white"> <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=CSS3&logoColor=white"> <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white"> <img src="https://img.shields.io/badge/jQuery-0769AD.svg?style=for-the-badge&logo=jQuery&logoColor=white">                                                                                                                                                                                                                                                                                                             |


<br/><br/>
# 🎨 ERD
<img src="https://github.com/user-attachments/assets/a68fdd74-435f-4905-861d-56ce5254ff37" alt="FilmFlyERD" style="max-width: 100%;">


<br/><br/>
# 🖥️ Project Architecture
<img src="https://github.com/user-attachments/assets/9a866157-3e9d-406d-9a6c-d05dfc3782d8" alt="FilmFly-아키텍처" style="max-width: 100%;">


<br/><br/>
# 🖥️ Troubleshooting
## - 도커 컨테이너 간 통신 문제
https://nicemouse.tistory.com/112
- Docker Compose를 이용해 Redis와 Spring Boot 애플리케이션을 각각 도커로 띄웠지만, Spring Boot에서는 Redis 연결 오류가 발생
- Spring Boot와 Redis가 하나의 EC2안에 실행되어 있으니 localhost라고 설정을 해서 문제
- 컨테이너들을 하나의 네트워크로 연결

## - 인증코드 이메일 발송을 비동식 방식으로 개선
https://nicemouse.tistory.com/107
- 이메일 전송 작업으로 인해 약 3~4초 동안 사용자에게 응답이 지연되는 문제 발생
- @Async와 CompletableFuture을 사용해 비동기 방식으로 변경
- 3671ms → 32ms 시간 단축
## - 인덱스를 활용하여 조회 성능 개선
https://nicemouse.tistory.com/110
- 영화 72만개, 좋아요 1180만개 정도 넣으니 쿼리 속도가 20분이 넘음
- explain & explain analyze을 활용해서 쿼리 분석 후
  커버링 인덱스 & fulltext index & No Offset 페이징 적용
- 32258ms → 208ms 시간 단축


<br/><br/>
# 📋 API 명세서
<img src="https://github.com/user-attachments/assets/8566c881-7a28-4061-b59a-a176d47c1538" alt="FilmFlyAPI" style="max-width: 100%;">
[API Page](https://www.notion.so/881c458a10c5490596763ab364969407?v=ebdbcfe22004485d833995bdaed92ac3)


<br/><br/>
# 🖼️ 프로젝트 상세 이미지
<img src="https://github.com/user-attachments/assets/9f8ae500-ba16-4136-a4a2-ca8654ed6192" alt="FilmFlyLogo" style="width: 100px;">

**메인 페이지**
<img src="https://github.com/user-attachments/assets/a45ee213-ec3a-4ccd-ad42-130a96f3982b" alt="" style="max-width: 100%;">

<br/>
<details>
<summary><b>페이지 상세 소개</b></summary> 
<br/><b>로그인</b>
<img src="https://github.com/user-attachments/assets/e6cbe27f-e744-450b-aa14-33506d0b5d15" alt="" style="max-width: 100%;">

<br/><b>회원가입</b>
<img src="https://github.com/user-attachments/assets/61e31c14-c49d-451f-bbcf-fa8d9461ee3a" alt="" style="max-width: 100%;">

<br/><b>영화</b>
<img src="https://github.com/user-attachments/assets/9b447bcc-3350-4801-a4f2-57f607b4c213" alt="" style="max-width: 100%;">

<br/><b>영화 검색</b>
<img src="https://github.com/user-attachments/assets/2711a70a-1225-46b4-b4a3-342016120359" alt="" style="max-width: 100%;">

<br/><b>영화 상세</b>
<img src="https://github.com/user-attachments/assets/d467ef20-8ef0-4ebd-9c45-62b061a35b16" alt="" style="max-width: 100%;">

<br/><b>영화 상세 보관함</b>
<img src="https://github.com/user-attachments/assets/d460d1e2-8ded-455b-884e-b548138d1ed1" alt="" style="max-width: 100%;">

<br/><b>영화 리뷰 작성</b>
<img src="https://github.com/user-attachments/assets/28e987aa-c6a8-4c64-921e-020e4e963c25" alt="" style="max-width: 100%;">

<br/><b>영화 리뷰</b>
<img src="https://github.com/user-attachments/assets/72a5db69-460b-4932-966b-a232c8e4aafa" alt="" style="max-width: 100%;">

<br/><b>최신 게시물</b>
<img src="https://github.com/user-attachments/assets/55d52b7e-ac5a-4ba7-85c6-9871b2a80b70" alt="" style="max-width: 100%;">

<br/><b>게시물 작성</b>
<img src="https://github.com/user-attachments/assets/dc14a68e-2d21-4852-be69-6cc0d50f4ab6" alt="" style="max-width: 100%;">

<br/><b>게시물</b>
<img src="https://github.com/user-attachments/assets/63303bfc-9fe2-4071-a1d6-29292b8b621d" alt="" style="max-width: 100%;">

<br/><b>최신 리뷰</b>
<img src="https://github.com/user-attachments/assets/62acf464-e1ef-44b4-ba07-8d101e8b17c0" alt="" style="max-width: 100%;">

<br/><b>신고 및 차단</b>
<img width="1280" alt="FilmFly-신고 차단" src="https://github.com/user-attachments/assets/6a848942-46c9-4cc7-9a7c-3b2a57387e18">
</details>

<br/>
<details>
<summary><b>마이 페이지</b></summary> 
<br/><b>마이페이지</b>
<img src="https://github.com/user-attachments/assets/fa6115a8-9788-4aeb-981c-bb6541ce3080" alt="" style="max-width: 100%;">

<br/><b>마이페이지 - 보관함</b>
<img src="https://github.com/user-attachments/assets/80f1b576-94e3-47d9-a942-50926602f211" alt="" style="max-width: 100%;">

<br/><b>마이페이지 - 보관함 상세</b>
<img src="https://github.com/user-attachments/assets/de5a6243-1175-4c89-8ad6-50bbb9822979" alt="" style="max-width: 100%;">

<br/><b>마이페이지 - 리뷰 목록</b>
<img src="https://github.com/user-attachments/assets/77c59c3c-91b0-4f4a-8523-d1bdddd24caa" alt="" style="max-width: 100%;">

<br/><b>마이페이지 - 좋아요</b>
<img src="https://github.com/user-attachments/assets/76e8c2c9-021e-4c91-b89a-f74d72903269" alt="" style="max-width: 100%;">
</details>

<br/>
<details>
<summary><b>관리자 페이지</b></summary> 
<br/><b>관리자 페이지 - 유저 관리</b>
<img src="https://github.com/user-attachments/assets/e964637e-0641-4e4b-b486-99ed7918b714" alt="" style="max-width: 100%;">

<br/><b>관리자 페이지 - 유저 상세</b>
<img src="https://github.com/user-attachments/assets/a715613d-10aa-413c-ad14-6d7e9cca565d" alt="" style="max-width: 100%;">

<br/><b>관리자 페이지 - 신고 관리</b>
<img src="https://github.com/user-attachments/assets/da9b3320-2072-4490-ad3b-0fb8f9e8c4f1" alt="" style="max-width: 100%;">

<br/><b>관리자 페이지 - 신고 상세</b>
<img src="https://github.com/user-attachments/assets/60d86650-9866-4dee-aee1-59ab948308ec" alt="" style="max-width: 100%;">
</details>
🙏🙏🤝🎉✨🎟️🎫🎁🖼️🎨🛠️⚙️🖥️💻🪪🎥🎬📽️📺✏️🗓️📋📌


<br/><br/>

[//]: # (# 🗃️ Code Convention)

[//]: # (<details>)

[//]: # (  <summary>Code Convention</summary>)

[//]: # ()
[//]: # (  -------)

[//]: # (<details>)

[//]: # (  <summary>Controller 작성 방법</summary>)

[//]: # ()
[//]: # (```java)

[//]: # (@RequestMapping&#40;"/review"&#41;)

[//]: # ()
[//]: # (@PatchMapping&#40;"/{reviewId}"&#41;)

[//]: # (public ResponseEntity<DataResponseDto<ReviewResponseDto>> updateReview&#40;)

[//]: # (    @AuthenticationPrincipal UserDetailsImpl userDetails,)

[//]: # (    @Valid @RequestBody ReviewUpdateRequestDto requestDto,)

[//]: # (    @PathVariable Long reviewId)

[//]: # (&#41; {)

[//]: # (    ReviewResponseDto responseDto = reviewService.updateReview&#40;userDetails.getUser&#40;&#41;, requestDto, reviewId&#41;;)

[//]: # (    return ResponseUtils.success&#40;responseDto&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (1. 매개변수 순서)

[//]: # (    - @AuthenticationPrincipal → @RequestBody → @PathVariable → @RequestParam)

[//]: # (3. Controller 반환 타입)

[//]: # (    - ResponseEntity<DataResponseDto<T>> 혹은 ResponseEntity<MessageResponseDto>)

[//]: # (    - ResponseUtils.success&#40;data&#41; 혹은 ResponseUtils.success&#40;&#41; 를 호출하여 반환)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>Service 작성 방법</summary>)

[//]: # ()
[//]: # (```java)

[//]: # (@Transactional // 반드시 붙이기)

[//]: # (public ReviewResponseDto updateReview&#40;User loginUser, ReviewUpdateRequestDto requestDto, Long reviewId&#41; {)

[//]: # (    Review findReview = reviewRepository.findByIdOrElseThrow&#40;reviewId&#41;;)

[//]: # ()
[//]: # (    // 수정하려는 리뷰가 내가 작성한 리뷰인지 검사)

[//]: # (    findReview.checkReviewOwner&#40;loginUser&#41;; // 유효성 검사는 엔티티에)

[//]: # ()
[//]: # (    findReview.updateReview&#40;requestDto&#41;;)

[//]: # (    return ReviewResponseDto.fromEntity&#40;findReview.getUser&#40;&#41;, findReview&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (1. 메서드 이름은 Controller랑 똑같이)

[//]: # (2. @Transactional 혹은 @Transactional&#40;readOnly = true&#41; 반드시 붙이기)

[//]: # (3. 유효성 검사 하는 코드는 Entity에 넣기 &#40;상황에 따라 알아서 하기&#41;)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>Repository 작성 방법</summary>)

[//]: # ()
[//]: # (```java)

[//]: # (public interface ReviewRepository extends JpaRepository<Review, Long> {)

[//]: # ()
[//]: # (	default Review findByIdOrElseThrow&#40;Long reviewId&#41; {)

[//]: # (	    return findById&#40;reviewId&#41;)

[//]: # (	        .orElseThrow&#40;&#40;&#41; -> new NotFoundException&#40;ResponseCodeEnum.REVIEW_NOT_FOUND&#41;&#41;;)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (1. findById&#40;&#41;는 `default`를 사용해서 `findByIdOrElse&#40;&#41;`로 이름 짓기)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>Entity 작성 방법</summary>)

[//]: # ()
[//]: # (```java)

[//]: # (@Entity)

[//]: # (@Getter)

[//]: # (@NoArgsConstructor&#40;access = AccessLevel.PROTECTED&#41;)

[//]: # (public class Review extends TimeStampEntity {)

[//]: # ()
[//]: # (    // 생략)

[//]: # ()
[//]: # (    @Column&#40;nullable = false&#41;)

[//]: # (    private String title;)

[//]: # ()
[//]: # (    @Column&#40;nullable = false&#41;)

[//]: # (    private String content;)

[//]: # ()
[//]: # (    @Column&#40;nullable = false&#41;)

[//]: # (    private Float rating;)

[//]: # ()
[//]: # (    // 생략)

[//]: # (    )
[//]: # (    // 생성자 대신 @Builder 사용)

[//]: # (    @Builder)

[//]: # (    public Review&#40;User user, Movie movie, String title, String content, Float rating&#41; {)

[//]: # (        this.user = user;)

[//]: # (        this.movie = movie;)

[//]: # (        this.title = title;)

[//]: # (        this.content = content;)

[//]: # (        this.rating = rating;)

[//]: # (        this.goodCount = 0L;)

[//]: # (        this.badCount = 0L;)

[//]: # (    })

[//]: # ()
[//]: # (		// @Setter 대신 이름을 붙여서 사용)

[//]: # (    public void updateReview&#40;ReviewUpdateRequestDto requestDto&#41; {)

[//]: # (        if &#40;requestDto.getTitle&#40;&#41; != null&#41; this.title = requestDto.getTitle&#40;&#41;;)

[//]: # (        if &#40;requestDto.getContent&#40;&#41; != null&#41; this.content = requestDto.getContent&#40;&#41;;)

[//]: # (        if &#40;requestDto.getRating&#40;&#41; != null&#41; this.rating = requestDto.getRating&#40;&#41;;)

[//]: # (    })

[//]: # (    )
[//]: # (    // 유효성 검사)

[//]: # (    public void checkReviewOwner&#40;User loginUser&#41; {)

[//]: # (        if &#40;!Objects.equals&#40;this.user.getId&#40;&#41;, loginUser.getId&#40;&#41;&#41;&#41; {)

[//]: # (            throw new NotOwnerException&#40;ResponseCodeEnum.REVIEW_NOT_OWNER&#41;;)

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (1. @NoArgsConstructor 는 무조건 `&#40;access = AccessLevel.PROTECTED&#41;` 달아주기)

[//]: # (2. `@Setter사용 절대 금지`  )

[//]: # (3. 생성자 대신 @Builder 사용하기)

[//]: # (4. Service에서 하던 유효성 검사는 엔티티에 작성 &#40;Service의 코드 간소화&#41;)

[//]: # (5. 필요에 따라 `@Column&#40;nullable = false&#41;` 옵션 달아주기)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>메서드명 규칙</summary>)

[//]: # ()
[//]: # (- CRUD)

[//]: # (    1. 생성 : create 로 시작 ex&#41; `createReview`)

[//]: # (    2. 조회 : get 으로 시작 ex&#41; `getReview`)

[//]: # (        1. List인 경우 getList… 로 시작 ex&#41; `getListReview`)

[//]: # (        2. page인 경우 getPage… 로 시작 ex&#41; `getPageReview`)

[//]: # (    3. 수정 : update 로 시작 ex&#41; `updateReview`)

[//]: # (    4. 삭제 : delete 로 시작 ex&#41; `deleteReview`)

[//]: # (- DTO)

[//]: # (    )
[//]: # (    Entity + 기능 + Request 혹은 Response + Dto)

[//]: # (    ex &#41; `ReviewUpdateRequestDto` | `ReviewResponseDto`)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>RequestDto → Entity | Entity → ResponseDto 변환 방법</summary>)

[//]: # ()
[//]: # (- `@Setter` 사용 금지)

[//]: # (- RequestDto → Entity)

[//]: # (RequestDto 안에 `toEntity` 생성)

[//]: # (    )
[//]: # (    ```java)

[//]: # (    @Getter)

[//]: # (    public class ReviewCreateRequestDto {)

[//]: # (     )
[//]: # (        // 생략)

[//]: # (    )
[//]: # (        // static 없음)

[//]: # (        public Review toEntity&#40;User user, Movie movie&#41; {)

[//]: # (            return Review.builder&#40;&#41;)

[//]: # (                .title&#40;this.title&#41;)

[//]: # (                .content&#40;this.content&#41;)

[//]: # (                .rating&#40;this.rating&#41;)

[//]: # (                .movie&#40;movie&#41;)

[//]: # (                .user&#40;user&#41;)

[//]: # (                .build&#40;&#41;;)

[//]: # (        })

[//]: # (    })

[//]: # (    ```)

[//]: # ()
[//]: # (    ## Service에서 사용법)

[//]: # (    )
[//]: # (    ```java)

[//]: # (    @Transactional)

[//]: # (    public ReviewResponseDto saveReview&#40;User loginUser, ReviewCreateRequestDto requestDto&#41; {)

[//]: # (    )
[//]: # (        // 생략)

[//]: # (    )
[//]: # (        Review review = requestDto.toEntity&#40;loginUser, findMovie&#41;; )

[//]: # (    )
[//]: # (        // 생략)

[//]: # (    })

[//]: # (    ```)

[//]: # (    )
[//]: # (- Entity → ResponseDto)

[//]: # (ResponseDto 안에 `fromEntity` 만들기)

[//]: # (    )
[//]: # (    ```java)

[//]: # (    @Getter)

[//]: # (    @Builder)

[//]: # (    public class ReviewResponseDto {)

[//]: # (    )
[//]: # (        // 생략)

[//]: # (    )
[//]: # (        // static 있음 !!!!)

[//]: # (        public static ReviewResponseDto fromEntity&#40;User user, Review review&#41; {)

[//]: # (            return ReviewResponseDto.builder&#40;&#41;)

[//]: # (                .id&#40;review.getId&#40;&#41;&#41;)

[//]: # (                .nickname&#40;user.getNickname&#40;&#41;&#41;)

[//]: # (                .pictureUrl&#40;user.getPictureUrl&#40;&#41;&#41;)

[//]: # (                .rating&#40;review.getRating&#40;&#41;&#41;)

[//]: # (                .title&#40;review.getTitle&#40;&#41;&#41;)

[//]: # (                .content&#40;review.getContent&#40;&#41;&#41;)

[//]: # (                .goodCount&#40;review.getGoodCount&#40;&#41;&#41;)

[//]: # (                .badCount&#40;review.getBadCount&#40;&#41;&#41;)

[//]: # (                .createdAt&#40;review.getUpdatedAt&#40;&#41;&#41;)

[//]: # (                .build&#40;&#41;;)

[//]: # (        })

[//]: # (    })

[//]: # (    ```)

[//]: # (    )
[//]: # (    ## Service 에서 사용법)

[//]: # (    )
[//]: # (    ```java)

[//]: # (    @Transactional)

[//]: # (    public ReviewResponseDto saveReview&#40;User loginUser, ReviewCreateRequestDto requestDto&#41; {)

[//]: # (    )
[//]: # (        // 생략)

[//]: # (    )
[//]: # (        return ReviewResponseDto.fromEntity&#40;loginUser, savedReview&#41;;)

[//]: # (    })

[//]: # (    ```)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>환경변수 관리</summary>)

[//]: # ()
[//]: # (- env 파일로 관리)

[//]: # (    - 파일 경로 : `src/main/resources/properties/env.properties`)

[//]: # (    )
[//]: # (    ```java)

[//]: # (    DB_URL=jdbc:mysql://localhost:3306/film_fly)

[//]: # (    DB_USERNAME=root)

[//]: # (    ```)

[//]: # (    )
[//]: # (- config 설정 : `src/main/domain/config/AppConfig`)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>Directory Package 구조</summary>)

[//]: # ()
[//]: # (- 도메인형 구조)

[//]: # (    - 각각의 도메인 별로 패키지 분리가 가능하여 관리에 있어서 계층형 방식보다 직관적)

[//]: # (    - 이러한 도메인 구조는 낮은 의존성을 갖기 유리해 코드의 재활용성이 향상됨)

[//]: # (    - 기능별로 분리되어 프로젝트 확장 및 유지보수 유리)

[//]: # (    )
[//]: # (    ```jsx)

[//]: # (    com)

[//]: # (     ㄴ projectGroup)

[//]: # (         ㄴ projectTitle)

[//]: # (             ㄴ domain)

[//]: # (             |   ㄴ user)

[//]: # (             |   |   ㄴ controller)

[//]: # (             |   |   ㄴ application)

[//]: # (             |   |   ㄴ dao)

[//]: # (             |   |   ㄴ domain)

[//]: # (             |   |   ㄴ dto)

[//]: # (             |   ㄴ video)

[//]: # (             |   |   ㄴ api)

[//]: # (             |   |   ㄴ application)

[//]: # (             |   |   ㄴ dao)

[//]: # (             |   |   ㄴ domain)

[//]: # (             |   |   ㄴ dto)

[//]: # (             |   ...)

[//]: # (             ㄴ global)

[//]: # (                 ㄴ auth)

[//]: # (                 ㄴ common)

[//]: # (                 ㄴ config)

[//]: # (                 ㄴ error)

[//]: # (                 ㄴ infra)

[//]: # (                 ㄴ util)

[//]: # (    ```)

[//]: # (    )
[//]: # (- 계층형  구조)

[//]: # (    )
[//]: # (    ```jsx)

[//]: # (    com)

[//]: # (     ㄴ projectGroup)

[//]: # (         ㄴ projectTitle)

[//]: # (             ㄴ config)

[//]: # (             ㄴ controller)

[//]: # (             ㄴ service)

[//]: # (             ㄴ repository)

[//]: # (             ㄴ security)

[//]: # (             ㄴ exception)

[//]: # (    ```)

[//]: # (    )
[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>HTTP Request 테스트 Tool</summary>)

[//]: # ()
[//]: # (- Spring HTTP Request 사용)

[//]: # (    - PostMan 대비 장점)

[//]: # (        - 테스트 속도 향상)

[//]: # (        - 테스트 코드 접근성 향상)

[//]: # (        - 협업 능력 향상 &#40;IntelliJ Code With Me 활용&#41;)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>정적 팩토리 메서드 패턴</summary>)

[//]: # ()
[//]: # (- https://inpa.tistory.com/entry/GOF-💠-정적-팩토리-메서드-생성자-대신-사용하자)

[//]: # (- 메서드 이름은 `from` 혹은 `of`로 시작하거나 명확한 이름이 있다면 명확하게 네이밍)

[//]: # (- Entity를 parameter로 받아와야함.)

[//]: # (- 정적 팩토리 메서드 패턴 사용 예시)

[//]: # ()
[//]: # (```java)

[//]: # (@Getter)

[//]: # (@Builder)

[//]: # (public class OfficeBoardResponseDto {)

[//]: # ()
[//]: # (		// 생략)

[//]: # ()
[//]: # (		public static OfficeBoardResponseDto fromEntity&#40;OfficeBoard officeBoard&#41;{    )

[//]: # (				return OfficeBoardResponseDto.*builder*&#40;&#41;)

[//]: # (						.id&#40;officeBoard.getId&#40;&#41;&#41;)

[//]: # (						.title&#40;officeBoard.getTitle&#40;&#41;&#41;)

[//]: # (						.content&#40;officeBoard.getContent&#40;&#41;&#41;)

[//]: # (						.nickName&#40;officeBoard.getUser&#40;&#41;)

[//]: # (						.getNickname&#40;&#41;&#41;)

[//]: # (						.hits&#40;officeBoard.getHits&#40;&#41;&#41;)

[//]: # (						.goodCount&#40;officeBoard.getGoodCount&#40;&#41;&#41;)

[//]: # (						.createdAt&#40;officeBoard.getUpdatedAt&#40;&#41;&#41;)

[//]: # (						.build&#40;&#41;;)

[//]: # (		})

[//]: # (})

[//]: # (```)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>Builder 패턴</summary>)

[//]: # ()
[//]: # (- 생성자를 만들 때 Builder 패턴을 사용)

[//]: # (- 필요한 것만 생성자로 사용)

[//]: # (- 필요한 것만 아래에 기본 초기 값 작성)

[//]: # (- Builder 패턴 사용 예시)

[//]: # ()
[//]: # (```java)

[//]: # (@Builder)

[//]: # (public Board&#40;User user, String title, String content&#41; {)

[//]: # (    this.user = user;)

[//]: # (    this.title = title;)

[//]: # (    this.content = content;)

[//]: # ()
[//]: # (    this.goodCount = 0L;)

[//]: # (    this.badCount = 0L;)

[//]: # (    this.hits = 0L;)

[//]: # (})

[//]: # (```)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>공통 예외 처리</summary>)

[//]: # ()
[//]: # (1. GlobalException을 상속을 받아 Custom Exception을 만든다.)

[//]: # (Custom Exception을 만들 때 다른 곳에서 공통으로 사용할 만 하게 `기능 위주`로 만든다.)

[//]: # ()
[//]: # (```java)

[//]: # (public class NotOwnerException extends GlobalException {)

[//]: # (    public NotOwnerException&#40;ResponseCodeEnum responseCodeEnum&#41; {)

[//]: # (        super&#40;responseCodeEnum&#41;;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (```java)

[//]: # (public void checkReviewOwner&#40;User loginUser&#41; {)

[//]: # (    if &#40;!Objects.equals&#40;this.user.getId&#40;&#41;, loginUser.getId&#40;&#41;&#41;&#41; {)

[//]: # (        throw new NotOwnerException&#40;ResponseCodeEnum.REVIEW_NOT_OWNER&#41;;)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>주석 처리</summary>)

[//]: # ()
[//]: # (메서드 위에 주석은 `JavaDoc`을 사용해 메서드 자체를 설명하는 주석 달기)

[//]: # ()
[//]: # (메서드 내부의 주석은 `//` 를 사용해 기능을 설명하는 주석 달기)

[//]: # ()
[//]: # (```java)

[//]: # (/**)

[//]: # (* 리뷰 수정)

[//]: # (*/)

[//]: # (@Transactional)

[//]: # (public ReviewResponseDto updateReview&#40;User loginUser, ReviewUpdateRequestDto requestDto, Long reviewId&#41; {)

[//]: # (    Review findReview = reviewRepository.findByIdOrElseThrow&#40;reviewId&#41;;)

[//]: # ()
[//]: # (    // 자기가 작성한 리뷰가 맞는지 체크)

[//]: # (    findReview.checkReviewOwner&#40;loginUser&#41;;)

[//]: # ()
[//]: # (    findReview.updateReview&#40;requestDto&#41;;)

[//]: # (    return ReviewResponseDto.fromEntity&#40;findReview.getUser&#40;&#41;, findReview&#41;;)

[//]: # (})

[//]: # (```)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>기능 구현하면 팀 노션에 Request, Response 정보 작성하기</summary>)

[//]: # ()
[//]: # (# Request)

[//]: # ()
[//]: # (```json)

[//]: # ({)

[//]: # (    "name":"호파스타",)

[//]: # (    "address":"서울시 광진구",)

[//]: # (    "category":"양식",)

[//]: # (    "description":"라구 파스타가 맛있음")

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (# Response)

[//]: # ()
[//]: # (```json)

[//]: # ({)

[//]: # (	"statusCode": 200,)

[//]: # (	"message": "가게 등록이 완료되었습니다.",)

[//]: # (	"data": {)

[//]: # (		"name": "호파스타 ",)

[//]: # (		"address": "서울시 광진구",)

[//]: # (		"categoryEnum": "WESTERN",)

[//]: # (		"description": "라구 파스타가 맛있음",)

[//]: # (		"createdAt": "2024-06-24T18:52:23.105005")

[//]: # (	})

[//]: # (})

[//]: # (```)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>AWS</summary>)

[//]: # ()
[//]: # (- AWS EC2 Linux Ubuntu)

[//]: # (- RDS)

[//]: # (    - Mysql)

[//]: # (    - DynamoDB : 교체 예정)

[//]: # (- Domain)

[//]: # (    - 구매 : 가비아)

[//]: # (        - [gabia 웹을 넘어 클라우드로. 가비아]&#40;https://www.gabia.com/?utm_source=google&utm_medium=cpc&utm_term=%EA%B0%80%EB%B9%84%EC%95%84&utm_campaign=%EA%B0%80%EB%B9%84%EC%95%84&#41;)

[//]: # (- Elastic Load Balancing)

[//]: # (    - 인스턴스가 예기치 못하게 종료되어도 서버를 유지하기 위해 설정)

[//]: # (- 탄력적 IP)

[//]: # (    - 로드 밸런서로 할당되는 IP를 고정시키기 위해 설정)

[//]: # (- S3)

[//]: # (    - 이미지, 영상 등 파일 저장소)

[//]: # (- Redis)

[//]: # (    - 동시성 제어)

[//]: # (</details>)

[//]: # (</details>)

[//]: # ()
[//]: # ()
[//]: # (<br/><br/>)

[//]: # (# 🤝 Github Rules)

[//]: # (<details>)

[//]: # (  <summary>1. 이슈</summary>)

[//]: # ( )
[//]: # ( - 메인 기능에 대한 이슈를 만들고 세부 이슈를 만들기 ex&#41; `[FEAT] 리뷰 기능` )

[//]: # ( - Assignees, Labels, Projects 달아 주기)

[//]: # ()
[//]: # (<img src="https://github.com/user-attachments/assets/c2c57018-1efa-4ed6-8f30-a918c5803247" alt="FilmFly-GithubRules1" style="max-width: 100%;">)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>2. 브랜치</summary>)

[//]: # ( )
[//]: # (- 이슈를 만들고 이슈창 오른쪽에 Development에서 `create a branch` 를 클릭해서 기본으로 정해주는 이름으로 브랜치 만들기)

[//]: # ()
[//]: # (- 세부 이슈라면? `Branch Source` 를 메인 브랜치로 선택하기)

[//]: # ()
[//]: # (<img src="https://github.com/user-attachments/assets/cd6a6ea1-8cc1-4ae6-a08e-5c98b56f6ead" alt="FilmFly-GithubRules2" style="max-width: 100%;">)

[//]: # ()
[//]: # (- main → dev → feat / refactor / fix)

[//]: # (    - **`feat/기능명` → 이케!**)

[//]: # ()
[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>3. 커밋 메세지</summary>)

[//]: # ( )
[//]: # (`[타입] 제목`)

[//]: # ()
[//]: # (| 타입 | 설명 |)

[//]: # (| --- | --- |)

[//]: # (| FEAT | 새로운 기능 추가 |)

[//]: # (| BUGFIX | 버그 해결 |)

[//]: # (| REFACTOR | 코드 리팩토링, )

[//]: # (새로운 기능/버그 해결 X |)

[//]: # (| TEST | 테스트 코드 작성 |)

[//]: # ()
[//]: # (`타입 [#이슈번호] : 제목`)

[//]: # ()
[//]: # (| 타입 | 설명 |)

[//]: # (| --- | --- |)

[//]: # (| Feat | 새로운 기능 추가 |)

[//]: # (| Fix | 버그 해결 |)

[//]: # (| Refactor | 코드 리팩토링, )

[//]: # (새로운 기능/버그 해결 X |)

[//]: # (| Move | 파일 옮김/정리 |)

[//]: # (| Rename | 파일/폴더 이름 수정 |)

[//]: # (| Remove | 파일/폴더 삭제 |)

[//]: # (| Test | 테스트 코드 작성 |)

[//]: # ()
[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (  <summary>4. Pull Request</summary>)

[//]: # ()
[//]: # (`기능만 입력` 더 설명할 내용이 있으면 안쪽에 적기)

[//]: # ()
[//]: # (세부 브랜치에서 메인 브랜치로 PR을 날리고 메인 브랜치의 기능이 다 끝나면 dev로 PR)

[//]: # (Assignees, Labels, Projects 달아 주기)

[//]: # (<img src="https://github.com/user-attachments/assets/df25e8ac-321a-4228-9bc7-48faea4da99a" alt="FilmFly-GithubRules3" style="max-width: 100%;">)

[//]: # (<img src="https://github.com/user-attachments/assets/d3fe3f80-0093-401c-a573-97832c5b17a4" alt="FilmFly-GithubRules4" style="max-width: 100%;">)

[//]: # (</details>)

[//]: # ()
[//]: # (<br/><br/>)

[//]: # (# ✍️ KPT 회고)

[//]: # (<details>)

[//]: # (    <summary>Keep - 현재 만족하고 있는 부분</summary>)

[//]: # (    <ul>)

[//]: # (        <li>123</li>)

[//]: # (        <li>456</li>)

[//]: # (    </ul>)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (    <summary>Problem - 불편하게 느끼는 부분</summary>)

[//]: # (    <ul>)

[//]: # (        <li>123</li>)

[//]: # (        <li>456</li>)

[//]: # (    </ul>)

[//]: # (</details>)

[//]: # ()
[//]: # (<details>)

[//]: # (    <summary>Try - Problem에 대한 해결책, 당장 실행 가능한 것</summary>)

[//]: # (    <ul>)

[//]: # (        <li>123</li>)

[//]: # (        <li>456</li>)

[//]: # (    </ul>)

[//]: # (</details>)
