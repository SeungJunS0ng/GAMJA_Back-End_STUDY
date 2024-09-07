package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 글 작성 처리
    public void write(Board board, MultipartFile file) throws Exception{
        // 프로젝트 경로를 가져옴
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        // UUID 생성
        UUID uuid = UUID.randomUUID();

        // 파일명 생성 (UUID + 원래 파일명)
        String fileName = uuid + "_" + file.getOriginalFilename();

        // 파일 저장 경로 설정
        File saveFile = new File(projectPath, fileName);

        // 파일 저장
        file.transferTo(saveFile);

        // Board 객체에 파일 정보 설정
        board.setFilename(fileName);
        board.setFilepath("/files/" + fileName);

        // DB에 저장
        boardRepository.save(board);
    }

    // 게시글 리스트 처리
    public Page<Board> boardList(Pageable pageable) {
        // 모든 게시글을 pageable 형식으로 반환
        return boardRepository.findAll(pageable);
    }

    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable) {
        // 제목에 검색 키워드가 포함된 게시글을 pageable 형식으로 반환
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    // 특정 게시글 불러오기
    public Board boardView(Integer id) {
        // id로 게시글을 조회
        return boardRepository.findById(id).get();
    }

    // 특정 게시글 삭제
    public void boardDelete(Integer id) {
        // id로 게시글을 삭제
        boardRepository.deleteById(id);
    }
}

