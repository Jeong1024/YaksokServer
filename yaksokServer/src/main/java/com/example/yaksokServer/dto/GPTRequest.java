package com.example.yaksokServer.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GPTRequest {
    private String model;
    private List<GPTMessage> messages;

    public GPTRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new GPTMessage("user", prompt));
    }
}
