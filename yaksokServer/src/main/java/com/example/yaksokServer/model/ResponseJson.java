package com.example.yaksokServer.model;

import lombok.Data;

@Data
public class ResponseJson<T> {
    private int code;
    private String message;
    private boolean success = true;
    private PillInfo pillData;
    private String gptPositiveTag;
    private String gptNegativeTag;
}

