package com.study.board.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 게시판 엔티티 클래스.
 *
 * JPA 엔티티라고 표시해야 해서 @Entity 어노테이션을 붙였음.
 * @Data 어노테이션으로 필드에 대한 getter와 setter를 자동으로 생성함.
 */
@Entity
@Data
public class Board {

    /**
     * 게시판의 고유 식별자.
     *
     * 이 필드를 엔티티의 기본키라고 표시해야 해서 @Id 어노테이션을 붙였음.
     * ID를 자동으로 생성해야 해서 @GeneratedValue 어노테이션을 사용했음. GenerationType.IDENTITY 전략을 사용하면 DB가 자동으로 ID를 생성해줌.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 게시판의 제목.
     */
    private String title;

    /**
     * 게시판의 내용.
     */
    private String content;

    /**
     * 게시판에 첨부된 파일의 이름.
     */
    private String filename;

    /**
     * 게시판에 첨부된 파일의 경로.
     */
    private String filepath;
}
