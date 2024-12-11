package com.example.yaksokServer.model;

import lombok.Data;

@Data
public class ImageResult {

    // 이미지 검색 결과
    private String rank; // 순위

    private String code; // 약 이름

    private String accuracy; // 정확도
}

