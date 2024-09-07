package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    private static final String FILE_DIRECTORY = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

    /**
     * 파일을 다운로드합니다.
     *
     * @param id 게시글 ID
     * @return 파일의 바이트 배열을 포함하는 ResponseEntity
     * @throws IOException 파일 읽기 오류 발생 시
     */
    public ResponseEntity<byte[]> downloadFile(Integer id) throws IOException {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));

        File file = new File(FILE_DIRECTORY, board.getFilename());
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);
    }

    /**
     * 게시글을 작성하고 파일을 저장합니다.
     *
     * @param board 게시글 정보
     * @param file 첨부 파일
     * @throws Exception 파일 저장 중 오류 발생 시
     */
    public void write(Board board, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }

        boardRepository.save(board);
    }

    /**
     * 게시글을 수정하고 파일을 업데이트합니다.
     *
     * @param id 게시글 ID
     * @param updatedBoard 수정된 게시글 정보
     * @param file 첨부 파일
     * @throws Exception 파일 저장 중 오류 발생 시
     */
    public void updateBoard(Integer id, Board updatedBoard, MultipartFile file) throws Exception {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);
        }

        board.setTitle(updatedBoard.getTitle());
        board.setContent(updatedBoard.getContent());

        boardRepository.save(board);
    }

    /**
     * 게시글 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 페이징 처리된 게시글 목록
     */
    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    /**
     * 검색어로 게시글 목록을 조회합니다.
     *
     * @param searchKeyword 검색어
     * @param pageable 페이징 정보
     * @return 검색어가 포함된 게시글 목록
     */
    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable) {
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    /**
     * 특정 게시글을 조회합니다.
     *
     * @param id 게시글 ID
     * @return 게시글
     */
    public Board boardView(Integer id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));
    }

    /**
     * 특정 게시글을 삭제합니다.
     *
     * @param id 게시글 ID
     */
    public void boardDelete(Integer id) {
        boardRepository.deleteById(id);
    }

    /**
     * 게시글을 수정합니다 (파일 없이).
     *
     * @param id 게시글 ID
     * @param title 수정된 제목
     * @param content 수정된 내용
     */
    public void updateBoard(Integer id, String title, String content) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));

        board.setTitle(title);
        board.setContent(content);
        boardRepository.save(board);
    }

    /**
     * 파일을 저장합니다.
     *
     * @param file 첨부 파일
     * @return 저장된 파일의 이름
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    private String saveFile(MultipartFile file) throws IOException {
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(FILE_DIRECTORY, fileName);

        file.transferTo(saveFile);
        return fileName;
    }
}
