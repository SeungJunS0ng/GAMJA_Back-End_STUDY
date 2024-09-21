package com.study.board.service;

import com.study.board.dto.BoardDTO; // BoardDTO를 가져옴
import com.study.board.entity.Board; // Board 엔티티를 가져옴
import com.study.board.repository.BoardRepository; // 게시물 관련 데이터베이스 작업을 위한 레포지토리
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입을 위해 사용
import org.springframework.data.domain.Page; // 페이지네이션을 위한 클래스
import org.springframework.data.domain.Pageable; // 페이지 요청을 위한 클래스
import org.springframework.stereotype.Service; // 이 클래스가 서비스임을 나타냄
import org.springframework.web.multipart.MultipartFile; // 파일 업로드를 위한 클래스

import java.io.File; // 파일 관련 클래스
import java.io.IOException; // 입출력 관련 예외
import java.util.UUID; // 고유한 ID 생성을 위한 클래스

@Service // 이 클래스가 서비스임을 나타냄
public class BoardService {

    @Autowired
    private BoardRepository boardRepository; // 게시물 관련 데이터베이스 작업을 위한 레포지토리

    // 파일 저장 경로 설정
    private static final String FILE_DIRECTORY = System.getProperty("user.dir") + File.separator +
            "src" + File.separator + "main" + File.separator + "resources" + File.separator +
            "static" + File.separator + "files";

    /**
     * 게시물 조회를 위한 메서드
     *
     * @param id 게시물 ID
     * @return 게시물 DTO
     */
    public BoardDTO boardView(Integer id) {
        // 게시물 ID로 게시물 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID")); // 게시물이 없으면 예외 발생

        // Board 엔티티를 BoardDTO로 변환하여 반환
        return convertToDTO(board);
    }

    /**
     * 게시물 작성 메서드
     *
     * @param boardDTO 게시물 데이터 전송 객체
     * @param file 첨부 파일
     * @throws Exception 파일 저장 중 오류 발생 시
     */
    public void write(BoardDTO boardDTO, MultipartFile file) throws Exception {
        Board board = new Board(); // 새로운 Board 엔티티 생성
        board.setTitle(boardDTO.getTitle());
        board.setContent(boardDTO.getContent());

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file); // 파일 저장 메서드 호출
            board.setFilename(fileName); // 파일 이름 설정
            board.setFilepath("/files/" + fileName); // 파일 경로 설정
        }

        // 게시물 데이터베이스에 저장
        boardRepository.save(board);
    }

    /**
     * 게시물 수정 메서드
     *
     * @param id 게시물 ID
     * @param boardDTO 수정된 게시물 데이터 전송 객체
     * @param file 첨부 파일
     * @throws Exception 파일 저장 중 오류 발생 시
     */
    public void updateBoard(Integer id, BoardDTO boardDTO, MultipartFile file) throws Exception {
        // 게시물 ID로 기존 게시물 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID")); // 게시물이 없으면 예외 발생

        board.setTitle(boardDTO.getTitle());
        board.setContent(boardDTO.getContent());

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file); // 파일 저장 메서드 호출
            board.setFilename(fileName); // 새 파일 이름 설정
            board.setFilepath("/files/" + fileName); // 새 파일 경로 설정
        }

        // 수정된 게시물 데이터베이스에 저장
        boardRepository.save(board);
    }

    /**
     * 게시글 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 페이징 처리된 게시글 목록
     */
    public Page<BoardDTO> boardList(Pageable pageable) {
        // 모든 게시물 목록을 페이징 처리하여 반환
        return boardRepository.findAll(pageable).map(this::convertToDTO);
    }

    /**
     * 검색어로 게시글 목록을 조회합니다.
     *
     * @param searchKeyword 검색어
     * @param pageable 페이징 정보
     * @return 검색어가 포함된 게시글 목록
     */
    public Page<BoardDTO> boardSearchList(String searchKeyword, Pageable pageable) {
        // 제목에 검색어가 포함된 게시물 목록을 페이징 처리하여 반환
        return boardRepository.findByTitleContaining(searchKeyword, pageable).map(this::convertToDTO);
    }

    /**
     * 특정 게시글을 삭제합니다.
     *
     * @param id 게시글 ID
     */
    public void boardDelete(Integer id) {
        // 게시물 ID로 게시물 삭제
        boardRepository.deleteById(id);
    }

    /**
     * Board 엔티티를 BoardDTO로 변환하는 메서드
     *
     * @param board 게시물 엔티티
     * @return 변환된 BoardDTO
     */
    private BoardDTO convertToDTO(Board board) {
        BoardDTO dto = new BoardDTO();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setFilename(board.getFilename());
        dto.setFilepath(board.getFilepath());
        return dto;
    }

    /**
     * 파일을 저장하는 메서드
     *
     * @param file 첨부 파일
     * @return 저장된 파일의 이름
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    private String saveFile(MultipartFile file) throws IOException {
        // UUID를 이용해 고유한 파일 이름 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename(); // 파일 이름에 UUID 추가
        File saveFile = new File(FILE_DIRECTORY, fileName); // 파일 저장 경로 설정

        // 파일을 지정한 경로에 저장
        file.transferTo(saveFile);
        return fileName; // 저장된 파일의 이름 반환
    }
}
