package com.study.board.repository;

import com.study.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 게시판 엔티티에 대한 데이터베이스 접근을 제공하는 레포지토리 인터페이스.
 *
 * JpaRepository를 확장하여 기본적인 CRUD 및 페이징 기능을 제공합니다.
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    /**
     * 제목에 검색 키워드가 포함된 게시물을 페이징하여 조회합니다.
     *
     * @param searchKeyword 검색할 제목의 키워드
     * @param pageable 페이징 정보
     * @return 제목에 검색 키워드가 포함된 게시물 목록 (페이징 처리됨)
     */
    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);
}