package com.sparta.filmfly.domain.movie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.filmfly.domain.movie.dto.*;
import com.sparta.filmfly.domain.movie.entity.*;
import com.sparta.filmfly.domain.movie.repository.CreditRepository;
import com.sparta.filmfly.domain.movie.repository.GenreRepository;
import com.sparta.filmfly.domain.movie.repository.MovieCreditRepository;
import com.sparta.filmfly.domain.movie.repository.MovieRepository;
import com.sparta.filmfly.domain.review.repository.ReviewRepository;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.CursorResponseDto;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.common.util.JsonFormatter;
import com.sparta.filmfly.global.exception.custom.detail.ApiRequestFailedException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final CreditRepository creditRepository;
    private final MovieCreditRepository movieCreditRepository;
    private final GenreRepository genreRepository;
    private final OkHttpClient httpClient;
    private final EntityManager em;

    // 이미지 경로 : https://image.tmdb.org/t/p/w220_and_h330_face/이미지.jpg
    // 크기 : w220_and_h330_face , w600_and_h900_bestv2 , w1280
    private final String baseUrl = "https://api.themoviedb.org";
    private final ReviewRepository reviewRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${TMDB_API_AUTHORIZATION}")
    private String TMDB_API_AUTHORIZATION;

    Map<Integer, Integer> map = Map.ofEntries(
        Map.entry(28,20),
        Map.entry(12,21),
        Map.entry(16,22),
        Map.entry(35,23),
        Map.entry(80,24),
        Map.entry(99,25),
        Map.entry(18,26),
        Map.entry(10751,27),
        Map.entry(14,28),
        Map.entry(36,29),
        Map.entry(27,30),
        Map.entry(10402,31),
        Map.entry(9648,32),
        Map.entry(10749,33),
        Map.entry(878,34),
        Map.entry(10770,35),
        Map.entry(53,36),
        Map.entry(10752,37),
        Map.entry(37,38)
    );

    /**
     * 영화 검색 (페이징)
     */
    public CursorResponseDto<MovieReactionsResponseDto> getPageMovieBySearchCond(
        MovieSearchCond searchOptions, Double lastPopularity, Pageable pageable
    ) {
        return movieRepository.getPageMovieBySearchCond(searchOptions, lastPopularity, pageable);
    }

    public List<MovieSimpleResponseDto> getMovieTrendList() {
        List<Movie> trendMovies = movieRepository.findTop20ByOrderByPopularityDesc();

        return trendMovies.stream()
            .map(MovieSimpleResponseDto::fromEntity)
            .toList();
    }

    @Transactional
    public List<ApiMovieResponseDto> apiRequestForMovie(Object apiDiscoverMovieRequestDto) {
        String movieUrl;
        if (apiDiscoverMovieRequestDto instanceof ApiDiscoverMovieRequestDto) {
            movieUrl = "/3/discover/movie";
        } else {
            movieUrl = "/3/search/movie";
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + movieUrl);
        Field[] fields = apiDiscoverMovieRequestDto.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // private 필드 접근 허용
            try {
                Object value = field.get(apiDiscoverMovieRequestDto);
                if (value != null) {
                    if (!(value instanceof Number && ((Number) value).doubleValue() == 0.0)) {
                        String fieldName = field.getName().replace("__", ".");
                        if (field.getType().isEnum()) {
                            // Enum 타입인 경우 getValue() 메서드를 호출하여 값을 쿼리 파라미터로 추가
                            Method getValueMethod = field.getType().getMethod("getValue");
                            Object enumValue = getValueMethod.invoke(value);
                            builder.queryParam(fieldName, enumValue);
                        } else {
                            // 일반 타입인 경우 값을 쿼리 파라미터로 추가
                            builder.queryParam(fieldName, value);
                        }
                    }
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.getStackTrace();
            }
        }

        String url = builder.toUriString();
        Request movieRequest = requestBuilder(url);
        try (Response response = httpClient.newCall(movieRequest).execute()) {
            if (!response.isSuccessful()) throw new ApiRequestFailedException(ResponseCodeEnum.API_REQUEST_FAILED);
            String body = response.body().string();
            String format = JsonFormatter.formatJson(body);

            // body 를 ApiMovieResponse 객체로 변환
            ApiMovieResponse apiMovieResponse = objectMapper.readValue(body, ApiMovieResponse.class);
            // 영화 api 조회 > 영화 목록 저장 및 업데이트 > 각 영화 > 배우 목록 저장 및 업데이트 > 중간 테이블 최신화 > 영화, 배우 테이블 동기화
            List<ApiMovieResponseDto> apiMovieResponseDtoList = apiMovieResponse.getResults();

            List<Movie> movieList = apiMovieResponseDtoList.stream()
                .map(ApiMovieResponseDto::toEntity)
                .toList();
            movieList = movieRepository.saveAll(movieList);

            List<MovieGenre> movieGenres = new ArrayList<>();
            for (ApiMovieResponseDto dto : apiMovieResponseDtoList) {
                Long movieId = (long) dto.getId();
                for (Integer genreId : dto.getGenreIds()) {
                    MovieGenre build = MovieGenre.builder()
                        .movie(em.getReference(Movie.class, movieId))
                        .genre(em.getReference(Genre.class, map.get(genreId)))
                        .build();
                    movieGenres.add(build);
                }
            }

            for (Movie movie : movieList) {
                for (MovieGenre movieGenre : movieGenres) {
                    if (movie.getId().equals(movieGenre.getMovie().getId())) {
                        movie.getMovieGenres().add(movieGenre);
                    }
                }
            }

            for (Movie movie : movieList) {
                // 영화 배우 목록 API 호출
                Request creditsRequest = requestBuilder(String.format("%s/3/movie/%d/credits", baseUrl, movie.getId()));
                Response creditsResponse = httpClient.newCall(creditsRequest).execute();
                if (!creditsResponse.isSuccessful())
                    throw new ApiRequestFailedException(ResponseCodeEnum.API_REQUEST_FAILED);
                // 배우 데이터 parsing
                String credits = creditsResponse.body().string();
                ApiCreditsResponse apiCreditsResponse = objectMapper.readValue(credits, ApiCreditsResponse.class);
                List<ApiCreditsResponseDto> apiCreditsResponseDtoList = apiCreditsResponse.getCast();

                // DTO 를 엔티티로 변환
                List<Credit> creditList = apiCreditsResponseDtoList.stream()
                        .map(ApiCreditsResponseDto::toEntity)
                        .collect(Collectors.toList());
                // Credit Entity 저장 : saveAll -> 이미 존재하는 데이터는 업데이트, 없으면 새로 생성
                creditList = creditRepository.saveAll(creditList);
                // 영화 list api 요청  가지고 옴
                // 영화 list forEach -> 영화 list 에 대해서 배우들 list 를 요청해서 가지고 옴
                // 영화 1 -> 배우 4,6,8,3
                // 영화 2 -> 배우 3,7,4,2,9
                // MovieCredit 엔티티 생성 및 저장
                creditList.forEach(credit -> {
                    if (!movieCreditRepository.existsByMovieAndCredit(movie, credit)) {
                        movieCreditRepository.save(MovieCredit.builder()
                                .movie(movie)
                                .credit(credit)
                                .build());
                    }
                });
            }
            return apiMovieResponseDtoList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 장르 API 요청, DB 갱신
     */
    public void apiGenresRequest(OriginLanguageEnum language) {
        // 장르 API 요청
        try {
            Request genresRequest = requestBuilder(String.format("%s/3/genre/movie/list?language=%s", baseUrl, language.getValue()));
            Response genresResponse = httpClient.newCall(genresRequest).execute();
            if (!genresResponse.isSuccessful()) {
                throw new ApiRequestFailedException(ResponseCodeEnum.API_REQUEST_FAILED);
            }
            // 장르 데이터 parsing
            assert genresResponse.body() != null;
            String genres = genresResponse.body().string();

            ApiGenresResponse apiGenresResponse = objectMapper.readValue(genres, ApiGenresResponse.class);
            List<GenresResponseDto> genresResponseDtoList = apiGenresResponse.getGenres();
            List<Genre> genreList = genresResponseDtoList.stream()
                    .map(GenresResponseDto::toEntity)
                    .collect(Collectors.toList());
            // 장르 DB 갱신
            genreRepository.saveAll(genreList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 영화 단건 조회
     */
    public MovieDetailSimpleResponseDto getMovie(UserDetailsImpl userDetails, Long movieId) {
        movieRepository.existsByIdOrElseThrow(movieId);

        MovieDetailSimpleResponseDto responseDto = movieRepository.getMovie(movieId);
        MovieReactionCheckResponseDto reactions = MovieReactionCheckResponseDto.setupFalse();
        if (userDetails != null) {
            reactions = movieRepository.checkMovieReaction(userDetails.getUser(), movieId);
        }
        Float avgRating = reviewRepository.getAverageRatingByMovieId(movieId);

        responseDto.updateReaction(reactions);
        responseDto.updateAvgRating(avgRating);

        return responseDto;
    }

    /**
     * 영화 수 반환
     */
    public long getMovieCount() {
        return movieRepository.count();
    }

    /**
     * 영화 장르 조회
     */
    public List<GenresResponseDto> getGenres() {
        return genreRepository.findAll().stream()
                .map(GenresResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    private Request requestBuilder(String url) {
        return new Builder()
            .url(url)
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", TMDB_API_AUTHORIZATION)
            .build();
    }
}