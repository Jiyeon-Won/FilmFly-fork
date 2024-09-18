package com.sparta.filmfly.domain.collection.service;

import com.sparta.filmfly.domain.collection.dto.*;
import com.sparta.filmfly.domain.collection.entity.Collection;
import com.sparta.filmfly.domain.collection.entity.MovieCollection;
import com.sparta.filmfly.domain.collection.repository.CollectionRepository;
import com.sparta.filmfly.domain.collection.repository.MovieCollectionRepository;
import com.sparta.filmfly.domain.movie.dto.MovieResponseDto;
import com.sparta.filmfly.domain.movie.entity.Movie;
import com.sparta.filmfly.domain.movie.repository.MovieRepository;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionService {
    private final CollectionRepository collectionRepository;
    private final MovieRepository movieRepository;
    private final MovieCollectionRepository movieCollectionRepository;

    /**
    * 보관함 생성
    */
    public CollectionWithUserResponseDto createCollection(User user, CollectionRequestDto collectionRequestDto) {
        // 이미 있는 보관함인지 확인
        collectionRepository.existsByUserIdAndNameOrElseThrow(user.getId(), collectionRequestDto.getName());

        // 보관함 생성, 저장
        Collection collection = Collection.builder()
                .user(user)
                .name(collectionRequestDto.getName())
                .content(collectionRequestDto.getContent())
                .build();
        collection = collectionRepository.save(collection);
        return CollectionWithUserResponseDto.fromEntity(collection);
    }

    /**
     * 보관함 단일 조회
     */
    public CollectionWithUserResponseDto getCollection(UserDetailsImpl userDetails, Long collectionId) {
        Collection collection = collectionRepository.findByIdOrElseThrow(collectionId);

        boolean isOwner = false;
        if (userDetails != null) {
            log.info("userDetails.userId: {}, collection.userid: {}", userDetails.getUser().getId(), collection.getUser().getId());
            if (Objects.equals(userDetails.getUser().getId(), collection.getUser().getId())) {
                isOwner = true;
            }
        }

        return CollectionWithUserResponseDto.fromEntity(collection, isOwner);
    }

    /**
    * 보관함 목록 조회
    */
    public List<CollectionStatusResponseDto> getAllCollection(User user, Long movieId) {
        // 자신이 생성한 보관함들 조회
        List<Collection> collectionList = collectionRepository.findAllByUser(user);

        List<CollectionStatusResponseDto> collectionStatusResponseDtoList;
        if(movieId != null) {
            // 영화 유무확인
            Movie movie = movieRepository.findByIdOrElseThrow(movieId);

            // 컬렉션 리스트를 스트림으로 변환하여 각 보관함에 대해 CollectionStatusResponseDto를 생성
            collectionStatusResponseDtoList = collectionList.stream()
                    .map(collection -> {
                        Boolean isRegistered = movieCollectionRepository.existsByCollectionIdAndMovieId(collection.getId(), movie.getId());
                        return CollectionStatusResponseDto.fromEntity(collection, isRegistered);
                    })
                    .collect(Collectors.toList()); // 결과를 리스트로 수집
        }
        else {
            // movie 정보가 없으면 일반적인 조회 isRegistered에 nyll 넣고 데이터 없이 그냥 전송
            collectionStatusResponseDtoList = collectionList.stream()
                    .map(collection -> {
                        return CollectionStatusResponseDto.fromEntity(collection, null);
                    })
                    .collect(Collectors.toList()); // 결과를 리스트로 수집
        }

        return collectionStatusResponseDtoList;
    }

    /**
     * 유저의 보관함 목록
     */
    public PageResponseDto<List<CollectionResponseDto>> getUsersCollections(Long userId, Pageable pageable) {
        Page<Collection> collectionList = collectionRepository.findAllByUserId(userId,pageable);

        return PageResponseDto.<List<CollectionResponseDto>>builder()
                .totalElements(collectionList.getTotalElements())
                .totalPages(collectionList.getTotalPages())
                .currentPage(collectionList.getNumber() + 1)
                .pageSize(collectionList.getSize())
                .data(collectionList.stream().map(CollectionResponseDto::fromEntity).toList())
                .build();
    }

    /**
     * 보관함 수정 권한 확인
     */
    public Boolean getCollectionIdUpdatePermission(User user, Long collectionId) {
        Collection collection = collectionRepository.findByIdOrElseThrow(collectionId);
        collection.validateOwner(user);
        return true;
    }

    /**
     * 보관함 수정 페이지 정보
     */
    public CollectionResponseDto forUpdateCollection(User user, Long collectionId) {
        Collection collection = collectionRepository.findByIdOrElseThrow(collectionId);
        collection.validateOwner(user);
        return CollectionResponseDto.fromEntity(collection);
    }

    /**
     * 보관함 수정
     */
    public CollectionResponseDto updateCollection(User user, CollectionRequestDto requestDto, Long collectionId) {
        Collection collection = collectionRepository.findByIdOrElseThrow(collectionId);
        collection.validateOwner(user);

        collection.updateCollection(requestDto);
        return CollectionResponseDto.fromEntity(collectionRepository.save(collection));
    }

    /**
    * 보관함 삭제
    */
    @Transactional
    public void deleteCollection(User user, Long collectionId) {
        Collection collection = collectionRepository.findByIdOrElseThrow(collectionId);
        collection.validateOwner(user);

        List<MovieCollection> movieCollectionList = movieCollectionRepository.findByCollection_id(collection.getId());
        movieCollectionList.forEach(movieCollection -> {
            movieCollectionRepository.delete(movieCollection);
        });
        movieCollectionRepository.deleteAllByCollectionId(collectionId); //보관함 외래키 연결된 영화들 부터 삭제

        collectionRepository.delete(collection);
    }

    /**
     * 보관함에 영화 등록
     */
    public void createMovieCollection(User user, MovieCollectionRequestDto movieCollectionRequestDto) {
        // 컬랙션 유무확인
        Collection collection = collectionRepository.findByIdOrElseThrow(movieCollectionRequestDto.getCollectionId());
        // 보관함 주인 확인
        collection.validateOwner(user);
        // 영화 유무확인
        Movie movie = movieRepository.findByIdOrElseThrow(movieCollectionRequestDto.getMovieId());
        // 영화 보관함 사전등록 여부 확인
        movieCollectionRepository.existsByCollectionIdAndMovieIdOrElseThrow(movieCollectionRequestDto.getCollectionId(), movie.getId());
        // 객체 생성 및 저장
        MovieCollection movieCollection = MovieCollection.builder()
                .collection(collection)
                .movie(movie)
                .build();
        movieCollectionRepository.save(movieCollection);
    }

    public PageResponseDto<List<MovieResponseDto>> getMovieCollection(Long collectionId,Pageable pageable) {
        // 보관함 여부 확인
        Collection collection = collectionRepository.findByIdOrElseThrow(collectionId);
        // 보관함 주인 확인
        //collection.validateOwner(user);
        // 보관함에서 영화목록 조회
        //List<MovieCollection> movieCollectionList = movieCollectionRepository.findByCollection_id(collection.getId());
        Page<MovieCollection> movieCollectionList = movieCollectionRepository.findByCollection_id(collection.getId(),pageable);
        List<MovieResponseDto> movieResponseDtoList = new ArrayList<>();
        for (MovieCollection movieCollection : movieCollectionList) {
            MovieResponseDto movieResponseDto = MovieResponseDto.fromEntity(movieCollection.getMovie());
            movieResponseDtoList.add(movieResponseDto);
        }

        return PageResponseDto.<List<MovieResponseDto>>builder()
                .totalElements(movieCollectionList.getTotalElements())
                .totalPages(movieCollectionList.getTotalPages())
                .currentPage(movieCollectionList.getNumber() + 1)
                .pageSize(movieCollectionList.getSize())
                .data(movieResponseDtoList)
                .build();
    }

    public void deleteMovieCollection(User user, MovieCollectionRequestDto movieCollectionRequestDto) {
        // 보관함 존재 확인
        Collection collection = collectionRepository.findByIdOrElseThrow(movieCollectionRequestDto.getCollectionId());
        // 보관함 주인 확인
        collection.validateOwner(user);
        // 영화 존재 확인
        Movie movie = movieRepository.findByIdOrElseThrow(movieCollectionRequestDto.getMovieId());
        // 영화-보관함 존재 확인
        MovieCollection movieCollection = movieCollectionRepository.findByCollectionIdAndMovieIdOrElseThrow(collection.getId(), movie.getId());
        // 보관함에서 영화 삭제
        movieCollectionRepository.delete(movieCollection);
    }
}