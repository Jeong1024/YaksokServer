package com.example.yaksokServer.model;

import jakarta.persistence.Id;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PillInfo {

    // 낱알 정보, api 통신용
    private String itemSeq;         // id: 품목기준코드
    
    private String entpName; // 업체명

    private String itemName; // 제품명

    private String efcyQesitm; // 효능

    private String useMethodQesitm; // 사용법

    private String atpnWarnQesitm; // 주의사항 경고

    private String atpnQesitm; // 주의사항

    private String intrcQesitm; // 상호작용

    private String seQesitm; // 부작용

    private String depositMethodQesitm; // 보관법

    private String openDe; // 공개일자

    private String updateDe; // 수정일자

    private String itemImage; // 낱알이미지
    
    private String bizrno; // 사업자 등록 번호

}