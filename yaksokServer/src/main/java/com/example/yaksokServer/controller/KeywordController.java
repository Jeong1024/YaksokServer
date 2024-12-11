package com.example.yaksokServer.controller;

/* GPT 키워드 추출 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.yaksokServer.dto.GPTRequest;
import com.example.yaksokServer.dto.GPTResponse;
import com.example.yaksokServer.model.PillInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.yaksokServer.model.ResponseJson;

@RestController
@RequestMapping("/GPT")
public class KeywordController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    private static Logger logger = LoggerFactory.getLogger(KeywordController.class);

    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestParam("prompt") String prompt){
        ResponseJson<PillInfo> res = new ResponseJson<>();
        GPTRequest request = new GPTRequest(model, prompt);
        GPTResponse response = template.postForObject(apiURL, request, GPTResponse.class);
        if (response != null) {
            res.setCode(200);
            res.setMessage(response.getChoices().get(0).getMessage().getContent());
            res.setSuccess(true);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        return null;
    }

    @GetMapping("/positive")
    public String extractPositive(@RequestParam("pillInfo") PillInfo pillInfo){
        String prompt =
                "(1) Extract positive summary keywords from user's input.\n" +
                "(2) Print five of the most trivial and popular summary keywords in Markdown format, Korean.\n" +
                "(3) When you print them, you must not print any explaination and punctuation marks. Only Summary Keywords.\n\n" +
                pillInfo.getEfcyQesitm() + "\n" +
                pillInfo.getUseMethodQesitm() + "\n" +
                pillInfo.getIntrcQesitm() + "\n" +
                pillInfo.getUseMethodQesitm();

        GPTRequest request = new GPTRequest(model, prompt);
        GPTResponse response = template.postForObject(apiURL, request, GPTResponse.class);
        if (response != null) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        return null;
    }

    @GetMapping("/negative")
    public String extractNegative(@RequestParam("pillInfo")PillInfo pillInfo){
        String prompt =
                "(1) Extract side effects from user's input.\n" +
                "(2) Print five of the most trivial and popular side effects in Markdown format, Korean.\n" +
                "(3) When you print them, you must not print any explaination and punctuation marks. Only Side Effects.\n\n" +
                pillInfo.getUseMethodQesitm() + "\n" +
                pillInfo.getAtpnWarnQesitm() + "\n" +
                pillInfo.getAtpnQesitm() + "\n" +
                pillInfo.getIntrcQesitm() + "\n" +
                pillInfo.getSeQesitm();

        GPTRequest request = new GPTRequest(model, prompt);
        GPTResponse response = template.postForObject(apiURL, request, GPTResponse.class);
        if (response != null) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        return null;
    }
}
