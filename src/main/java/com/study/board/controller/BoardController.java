package com.study.board.controller;

import com.study.board.dto.BoardDTO; // BoardDTO를 가져옴
import com.study.board.service.BoardService; // 게시물 관련 서비스 클래스를 가져옴
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입을 위해 사용
import org.springframework.data.domain.Page; // 페이지네이션을 위한 클래스
import org.springframework.data.domain.Pageable; // 페이지 요청을 위한 클래스
import org.springframework.data.domain.Sort; // 정렬 관련 클래스
import org.springframework.data.web.PageableDefault; // 페이지 설정을 위한 어노테이션
import org.springframework.http.HttpHeaders; // HTTP 헤더 관련 클래스
import org.springframework.http.MediaType; // 미디어 타입 관련 클래스
import org.springframework.http.ResponseEntity; // HTTP 응답을 나타내는 클래스
import org.springframework.stereotype.Controller; // 이 클래스가 컨트롤러임을 나타냄
import org.springframework.ui.Model; // 뷰에 데이터를 전달하기 위한 클래스
import org.springframework.web.bind.annotation.*; // 다양한 웹 관련 어노테이션을 가져옴
import org.springframework.web.multipart.MultipartFile; // 파일 업로드를 위한 클래스

import java.io.File; // 파일 관련 클래스
import java.io.FileNotFoundException; // 파일이 없을 때 발생하는 예외
import java.io.IOException; // 입출력 관련 예외
import java.nio.file.Files; // 파일 관련 유틸리티 클래스

@Controller // 이 클래스가 웹 요청을 처리하는 컨트롤러임을 나타냄
public class BoardController {

    @Autowired
    private BoardService boardService; // 게시물 관련 서비스 객체를 자동으로 생성

    private static final String FILE_DIRECTORY = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "files";

    // 게시물 작성 폼을 보여주는 메서드
    @GetMapping("/board/write")
    public String boardWriteForm() {
        return "boardwrite"; // 작성 폼 화면을 반환
    }

    // 게시물을 실제로 작성하는 메서드
    @PostMapping("/board/writepro")
    public String boardWritePro(@ModelAttribute BoardDTO boardDTO, Model model, @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        if (isBoardInvalid(boardDTO)) {
            return addMessage(model, "제목과 내용이 필요합니다.", "/board/write"); // 에러 메시지
        }
        boardService.write(boardDTO, file); // 게시물 저장
        return addMessage(model, "글 작성이 완료되었습니다.", "/board/list"); // 성공 메시지
    }

    // 게시물 목록을 보여주는 메서드
    @GetMapping("/board/list")
    public String boardList(Model model, @PageableDefault(page = 0, size = 6, sort = "id", direction = Sort.Direction.ASC) Pageable pageable, @RequestParam(required = false) String searchKeyword) {
        Page<BoardDTO> list = (searchKeyword == null) ? boardService.boardList(pageable) : boardService.boardSearchList(searchKeyword, pageable);
        addPaginationAttributes(model, list);
        model.addAttribute("list", list); // 게시물 목록을 화면에 전달

        if (searchKeyword != null && list.getContent().isEmpty()) {
            model.addAttribute("message", "검색 결과가 없습니다.");
        }

        return "boardlist"; // 게시물 목록 화면을 반환
    }

    // 특정 게시물의 상세 정보를 보여주는 메서드
    @GetMapping("/board/view")
    public String boardView(Model model, @RequestParam Integer id) {
        BoardDTO boardDTO = boardService.boardView(id); // ID로 게시물 조회
        model.addAttribute("board", boardDTO); // DTO를 모델에 추가
        return "boardview"; // 게시물 상세보기 화면을 반환
    }

    // 게시물을 삭제하는 메서드
    @GetMapping("/board/delete")
    public String boardDelete(@RequestParam Integer id) {
        boardService.boardDelete(id); // ID로 게시물 삭제
        return "redirect:/board/list"; // 삭제 후 게시물 목록으로 리다이렉트
    }

    // 게시물 수정 폼을 보여주는 메서드
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable Integer id, Model model) {
        BoardDTO boardDTO = boardService.boardView(id); // DTO로 게시물 조회
        model.addAttribute("board", boardDTO); // DTO를 모델에 추가
        model.addAttribute("currentFile", boardDTO.getFilename()); // 현재 파일 이름 추가
        return "boardmodify"; // 게시물 수정 폼 화면 반환
    }

    // 게시물을 수정하는 메서드
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable Integer id, @ModelAttribute BoardDTO boardDTO, @RequestParam(value = "file", required = false) MultipartFile file, Model model) throws Exception {
        if (isBoardInvalid(boardDTO)) {
            return addMessage(model, "제목과 내용이 필요합니다.", "/board/modify/" + id); // 에러 메시지
        }
        boardService.updateBoard(id, boardDTO, file); // 게시물 수정
        return "redirect:/board/list"; // 수정 후 게시물 목록으로 리다이렉트
    }

    // 게시물의 제목과 내용이 유효한지 확인하는 메서드
    private boolean isBoardInvalid(BoardDTO boardDTO) {
        return boardDTO.getTitle() == null || boardDTO.getTitle().trim().isEmpty() ||
                boardDTO.getContent() == null || boardDTO.getContent().trim().isEmpty();
    }

    // 메시지를 추가하는 메서드 (에러 및 성공 메시지 통합)
    private String addMessage(Model model, String message, String searchUrl) {
        model.addAttribute("message", message); // 메시지를 모델에 추가
        model.addAttribute("searchUrl", searchUrl); // 돌아갈 URL 추가
        return "message"; // 메시지 화면 반환
    }

    // 페이지 정보를 계산하여 모델에 추가하는 메서드
    private void addPaginationAttributes(Model model, Page<BoardDTO> list) {
        int nowPage = list.getPageable().getPageNumber() + 1; // 현재 페이지 번호
        int totalPages = list.getTotalPages(); // 전체 페이지 수
        int pageSize = 10; // 보여줄 페이지 번호 수

        int startPage = Math.max(nowPage - (pageSize / 2), 1); // 시작 페이지
        int endPage = Math.min(startPage + pageSize - 1, totalPages); // 끝 페이지

        if (endPage - startPage < pageSize - 1) {
            startPage = Math.max(endPage - (pageSize - 1), 1); // 페이지 범위 조정
        }

        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages); // 전체 페이지 수 추가
    }

    // 파일 다운로드를 처리하는 메서드
    @GetMapping("/board/download/{id}") // 클라이언트가 "/board/download/{id}" URL로 GET 요청을 할 때 이 메서드가 호출됨
    public ResponseEntity<byte[]> downloadFile(@PathVariable Integer id) throws IOException {
        BoardDTO boardDTO = boardService.boardView(id); // 게시물 ID로 게시물 조회
        File file = new File(FILE_DIRECTORY, boardDTO.getFilename()); // 파일 경로 생성

        if (!file.exists()) { // 파일이 존재하지 않으면
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath()); // 예외를 발생시켜 파일이 없음을 알림
        }

        byte[] fileBytes = Files.readAllBytes(file.toPath()); // 파일을 바이트 배열로 읽음

        return ResponseEntity.ok() // HTTP 상태 코드 200 OK로 응답 시작
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // 응답의 MIME 타입을 "application/octet-stream"으로 설정
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + boardDTO.getFilename() + "\"") // 다운로드 시 파일 이름 설정
                .body(fileBytes); // 파일의 바이트 배열을 응답 본문으로 설정
    }
}