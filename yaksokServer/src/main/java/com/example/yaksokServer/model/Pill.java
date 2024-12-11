package com.example.yaksokServer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "pill")
@Data
public class Pill {

    // 낱알 정보 + 키워드, db 저장용
    @Id
    @JsonIgnore
    @Column(name = "itemSeq", nullable = false)
    private String itemSeq;         // id: 품목기준코드

    private String entpName; // 업체명

    private String itemName; // 제품명

    @Column(length=2000)
    private String efcyQesitm; // 효능

    @Column(length=2000)
    private String useMethodQesitm; // 사용법

    @Column(length=2000)
    private String atpnWarnQesitm; // 주의사항 경고

    @Column(length=2000)
    private String atpnQesitm; // 주의사항

    @Column(length=2000)
    private String intrcQesitm; // 상호작용

    @Column(length=2000)
    private String seQesitm; // 부작용

    @Column(length=2000)
    private String depositMethodQesitm; // 보관법

    private String openDe; // 공개일자

    private String updateDe; // 수정일자

    private String itemImage; // 낱알이미지

    private String bizrno; // 사업자 등록 번호

    private String positive; // 효능

    private String negative; // 부작용, 주의사항 등

}