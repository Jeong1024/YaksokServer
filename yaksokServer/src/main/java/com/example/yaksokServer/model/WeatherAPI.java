package com.example.yaksokServer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherAPI {

    // 초단기실황, 시도별 실시간 측정정보 조회, api 통신용
    private String resultCode;  // 결과코드
    private String resultMsg;   // 결과메시지

    private String baseDate;    // 발표일자
    private String baseTime;    // 발표시각
    private String nx;          // 예보지점 X 좌표
    private String ny;          // 예보지점 Y 좌표
    private String sidoName;    // 시도 이름 (전국, 서울, 부산, 대구, 인천 광주, 대전...)

    private String T1H;         // 기온
    private String PTY;         // 강수 형태, 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7) 
    private String pm10;        // 미세먼지
    private String pm25;        // 초미세먼지
    private String khaiGrade;   // 통합대기환경지수, 좋음(1), 보통(2), 나쁨(3), 매우나쁨(4)
}
