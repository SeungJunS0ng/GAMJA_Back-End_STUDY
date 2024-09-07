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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class BoardController {


    // 서비스 클래스를 의존성 주입
    @Autowired
    private BoardService boardService;

    // 게시물 작성 폼을 반환하는 메소드
    @GetMapping("/board/write")
    public String boardWriteForm() {
        // 게시물 작성 폼을 반환
        return "boardwrite";
    }

    // 게시물 작성 처리를 하는 메소드
    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        // 서비스 클래스의 write 메소드를 호출하여 게시물 작성 처리
        boardService.write(board, file);

        // 모델에 메시지와 검색 URL을 추가
        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        // 메시지 페이지를 반환
        return "message";
    }

    // 게시물 목록을 가져오는 메소드
    @GetMapping("/board/list")
    public String boardList(Model model,
                            // 페이지네이션 설정 (페이지 번호, 한 페이지당 게시물 수, 정렬 기준, 정렬 방향)
                            @PageableDefault(page = 0, size = 6, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                            // 검색 키워드
                            String searchKeyword) {
        // 게시물 목록을 저장할 변수
        Page<Board> list = null;

        // 검색 키워드가 없으면 전체 게시물 목록을 가져옴
        if (searchKeyword == null) {
            // 서비스 클래스의 boardList 메소드를 호출하여 게시물 목록을 가져옴
            list = boardService.boardList(pageable);
        } else {
            // 검색 키워드가 있으면 검색 결과를 가져옴
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        // 현재 페이지 번호
        int nowPage = list.getPageable().getPageNumber() + 1;
        // 시작 페이지 번호 (현재 페이지 번호 - 4, 최소 1)
        int startPage = Math.max(nowPage - 4, 1);
        // 끝 페이지 번호 (현재 페이지 번호 + 5, 최대 전체 페이지 수)
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        // 모델에 게시물 목록, 현재 페이지 번호, 시작 페이지 번호, 끝 페이지 번호를 추가
        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // 게시물 목록 페이지를 반환
        return "boardlist";
    }

    // 게시물 상세보기 페이지를 반환하는 메소드
    @GetMapping("/board/view")
    public String boardView(Model model, Integer id) {
        // 모델에 게시물 상세 정보를 추가
        model.addAttribute("board", boardService.boardView(id));

        // 게시물 상세보기 페이지를 반환
        return "boardview";
    }

    // 게시물 삭제 처리를 하는 메소드
    @GetMapping("/board/delete")
    public String boardDelete(Integer id) {
        // 서비스 클래스의 boardDelete 메소드를 호출하여 게시물 삭제 처리
        boardService.boardDelete(id);

        // 게시물 목록 페이지로 리다이렉트
        return "redirect:/board/list";
    }

    // 게시물 수정 폼을 반환하는 메소드
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        // 모델에 게시물 상세 정보를 추가
        model.addAttribute("board", boardService.boardView(id));

        // 게시물 수정 폼을 반환
        return "boardmodify";
    }

    // 게시물 수정 처리를 하는 메소드
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, MultipartFile file) throws Exception {
        // 게시글 제목과 내용을 수정하기 위해 서비스 클래스의 updateBoard 메소드 호출
        boardService.updateBoard(id, board.getTitle(), board.getContent());

        // 파일이 있는 경우 처리 (파일 삭제, 업로드 등 필요한 작업)
        if (file != null && !file.isEmpty()) {
            // 파일 처리 로직 (기존 파일 삭제, 새로운 파일 업로드 등)
        }

        // 게시물 목록 페이지로 리다이렉트
        return "redirect:/board/list";
    }
}