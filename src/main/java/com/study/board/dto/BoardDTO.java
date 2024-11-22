package com.study.board.dto;

import lombok.Data;

/**
 * 게시물 데이터 전송 객체 (DTO).
 */
@Data
public class BoardDTO {

    private Integer id;           // 게시물 ID
    private String title;         // 게시물 제목
    private String content;       // 게시물 내용
    private String filename;      // 첨부된 파일 이름
    private String filepath;      // 첨부된 파일 경로
}