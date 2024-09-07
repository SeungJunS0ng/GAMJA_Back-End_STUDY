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
        if (board.getTitle() == null || board.getTitle().trim().isEmpty() ||
                board.getContent() == null || board.getContent().trim().isEmpty()) {
            model.addAttribute("message", "제목과 내용이 필요합니다.");
            model.addAttribute("searchUrl", "/board/write");
            return "message"; // 에러 메시지 뷰 반환
        }

        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message"; // 성공 메시지 뷰 반환
    }

    // 게시물 목록을 가져옴
    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 6, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                            @RequestParam(required = false) String searchKeyword) {
        Page<Board> list = (searchKeyword == null)
                ? boardService.boardList(pageable)
                : boardService.boardSearchList(searchKeyword, pageable);

        int nowPage = list.getPageable().getPageNumber() + 1;
        int totalPages = list.getTotalPages();
        int pageSize = 10;

        int startPage = Math.max(nowPage - (pageSize / 2), 1);
        int endPage = Math.min(startPage + pageSize - 1, totalPages);

        if (endPage - startPage < pageSize - 1) {
            startPage = Math.max(endPage - (pageSize - 1), 1);
        }

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardlist"; // 게시물 목록 뷰 반환
    }

    // 게시물 상세보기 페이지를 반환
    @GetMapping("/board/view")
    public String boardView(Model model, @RequestParam Integer id) {
        model.addAttribute("board", boardService.boardView(id));
        return "boardview"; // 게시물 상세보기 뷰 반환
    }

    // 게시물 삭제 처리
    @GetMapping("/board/delete")
    public String boardDelete(@RequestParam Integer id) {
        boardService.boardDelete(id);
        return "redirect:/board/list"; // 게시물 목록 페이지로 리다이렉트
    }

    // 게시물 수정 폼을 반환
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable Integer id, Model model) {
        model.addAttribute("board", boardService.boardView(id));
        return "boardmodify"; // 게시물 수정 폼 뷰 반환
    }

    // 게시물 수정 처리
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable Integer id, Board board, @RequestParam(value = "file", required = false) MultipartFile file, Model model) throws Exception {
        if (board.getTitle() == null || board.getTitle().trim().isEmpty() ||
                board.getContent() == null || board.getContent().trim().isEmpty()) {
            model.addAttribute("message", "제목과 내용이 필요합니다.");
            model.addAttribute("searchUrl", "/board/modify/" + id);
            return "message"; // 에러 메시지 뷰 반환
        }

        boardService.updateBoard(id, board.getTitle(), board.getContent());

        if (file != null && !file.isEmpty()) {
            // 파일 처리 로직 추가 가능
        }

        return "redirect:/board/list"; // 게시물 목록 페이지로 리다이렉트
    }
}
