package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    // 게시물 작성 폼을 반환
    @GetMapping("/board/write")
    public String boardWriteForm() {
        return "boardwrite"; // 작성 폼 뷰 반환
    }

    // 게시물 작성 처리
    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        // 제목 또는 내용이 비어있는지 확인
        if (board.getTitle() == null || board.getTitle().trim().isEmpty() ||
                board.getContent() == null || board.getContent().trim().isEmpty()) {
            // 에러 메시지를 모델에 추가
            model.addAttribute("message", "제목과 내용이 필요합니다.");
            model.addAttribute("searchUrl", "/board/write"); // 에러 발생 시 작성 폼으로 돌아갈 URL
            return "message"; // 에러 메시지 뷰 반환
        }

        // 게시물 작성 서비스 호출
        boardService.write(board, file);

        // 성공 메시지를 모델에 추가
        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list"); // 성공 후 게시물 목록으로 이동할 URL
        return "message"; // 성공 메시지 뷰 반환
    }

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 6, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(required = false) String searchKeyword) {
        // 검색 키워드가 있으면 검색 결과를 가져오고, 없으면 전체 게시물 목록을 가져옴
        Page<Board> list = (searchKeyword == null)
                ? boardService.boardList(pageable)
                : boardService.boardSearchList(searchKeyword, pageable);

        // 현재 페이지 번호와 총 페이지 수 계산
        int nowPage = list.getPageable().getPageNumber() + 1;
        int totalPages = list.getTotalPages();
        int pageSize = 10; // 페이지 번호를 몇 개 표시할지 결정

        // 현재 페이지를 기준으로 시작 페이지와 끝 페이지 계산
        int startPage = Math.max(nowPage - (pageSize / 2), 1);
        int endPage = Math.min(startPage + pageSize - 1, totalPages);

        // 페이지 범위가 부족하면 시작 페이지를 조정
        if (endPage - startPage < pageSize - 1) {
            startPage = Math.max(endPage - (pageSize - 1), 1);
        }

        // 모델에 게시물 목록 및 페이지 정보 추가
        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 검색 결과가 없으면 메시지 추가
        if (searchKeyword != null && list.getContent().isEmpty()) {
            model.addAttribute("message", "검색 결과가 없습니다.");
        }

        return "boardlist"; // 게시물 목록 뷰 반환
    }


    // 게시물 상세보기 페이지를 반환
    @GetMapping("/board/view")
    public String boardView(Model model, @RequestParam Integer id) {
        // ID로 게시물 조회
        model.addAttribute("board", boardService.boardView(id));
        return "boardview"; // 게시물 상세보기 뷰 반환
    }

    // 게시물 삭제 처리
    @GetMapping("/board/delete")
    public String boardDelete(@RequestParam Integer id) {
        // ID로 게시물 삭제
        boardService.boardDelete(id);
        return "redirect:/board/list"; // 게시물 목록 페이지로 리다이렉트
    }

    // 게시물 수정 폼을 반환
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable Integer id, Model model) {
        // ID로 게시물 조회
        model.addAttribute("board", boardService.boardView(id));
        return "boardmodify"; // 게시물 수정 폼 뷰 반환
    }

    // 게시물 수정 처리
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable Integer id, Board board, @RequestParam(value = "file", required = false) MultipartFile file, Model model) throws Exception {
        // 제목 또는 내용이 비어있는지 확인
        if (board.getTitle() == null || board.getTitle().trim().isEmpty() ||
                board.getContent() == null || board.getContent().trim().isEmpty()) {
            // 에러 메시지를 모델에 추가
            model.addAttribute("message", "제목과 내용이 필요합니다.");
            model.addAttribute("searchUrl", "/board/modify/" + id); // 에러 발생 시 수정 폼으로 돌아갈 URL
            return "message"; // 에러 메시지 뷰 반환
        }

        // 게시물 업데이트 서비스 호출
        boardService.updateBoard(id, board.getTitle(), board.getContent());

        if (file != null && !file.isEmpty()) {
            // 파일 처리 로직 추가 가능
        }

        return "redirect:/board/list"; // 게시물 목록 페이지로 리다이렉트
    }
}

