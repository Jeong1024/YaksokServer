package com.example.yaksokServer.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.yaksokServer.dto.GPTRequest;
import com.example.yaksokServer.dto.GPTResponse;
import com.example.yaksokServer.model.ImageResult;
import com.example.yaksokServer.model.Pill;
import com.example.yaksokServer.model.PillInfo;
import com.example.yaksokServer.model.ResponseJson;
import com.example.yaksokServer.model.WeatherAPI;
import com.example.yaksokServer.repository.PillRepository;
import com.example.yaksokServer.service.DBService;
import com.example.yaksokServer.service.ImageService;
import com.example.yaksokServer.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pill")
public class MainController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;
    @Autowired
    private PillRepository pillRepository;

    private static Logger logger = LoggerFactory.getLogger(MainController.class);

    private final DBService dbService;
    private final ImageService imageService;
    private final SearchService searchService;


    /* itemSeq 기반 검색 */
    @GetMapping(value = "/search/seq")
    public ResponseEntity<?> searchBySeq(@RequestParam("itemSeq") String itemSeq) {
        ResponseJson<PillInfo> response = new ResponseJson<>();
        Pill pill;
        PillInfo pillInfo = new PillInfo();

        if (dbService.findBySeq(itemSeq) != null){ /* db 내에 존재하는 경우 */
            pill = dbService.findBySeq(itemSeq);
            pillInfo = dbService.pillToPillInfo(pill);
            response.setCode(200);
            response.setMessage("db에 이미 저장됨.");
            response.setSuccess(true);
            response.setPillData(pillInfo);
            response.setGptPositiveTag(pill.getPositive());
            response.setGptNegativeTag(pill.getNegative());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            try {
                pillInfo = searchService.searchBySeq(itemSeq);
                logger.info(pillInfo.getItemName());
            } catch (Exception e) {
                logger.error(e.toString());
                response.setCode(500);
                response.setMessage("e약은요 api 통신 오류" + e.toString());
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            /* GPT 키워드 추출 */
            String positivePrompt = "(1) Extract positive summary keywords from user's input.\n" +
                    "(2) Print five of the most trivial and popular summary keywords in Korean. Each keywords distinguished by spacing.\n" +
                    "(3) When you print them, you must not print any explaination and punctuation marks. Only Summary Keywords.\n\n" +
                    pillInfo.getEfcyQesitm() + "\n" +
                    pillInfo.getUseMethodQesitm() + "\n" +
                    pillInfo.getIntrcQesitm() + "\n" +
                    pillInfo.getUseMethodQesitm();

            String negativePrompt = "(1) Extract side effects from user's input.\n" +
                    "(2) Print five of the most trivial and popular side effects in Korean. Each keywords distinguished by spacing.\n" +
                    "(3) When you print them, you must not print any explaination and punctuation marks. Only Side Effects.\n\n" +
                    pillInfo.getUseMethodQesitm() + "\n" +
                    pillInfo.getAtpnWarnQesitm() + "\n" +
                    pillInfo.getAtpnQesitm() + "\n" +
                    pillInfo.getIntrcQesitm() + "\n" +
                    pillInfo.getSeQesitm();

            GPTRequest positiveRequest = new GPTRequest(model, positivePrompt);
            GPTResponse positiveResponse = template.postForObject(apiURL, positiveRequest, GPTResponse.class);
            GPTRequest negativeRequest = new GPTRequest(model, negativePrompt);
            GPTResponse negativeResponse = template.postForObject(apiURL, negativeRequest, GPTResponse.class);

            if (positiveResponse != null && negativeResponse != null) {
                String positive = positiveResponse.getChoices().get(0).getMessage().getContent();
                String negative = negativeResponse.getChoices().get(0).getMessage().getContent();

                Pill pillResult = new Pill();
                pillResult.setItemSeq(pillInfo.getItemSeq());
                pillResult.setEntpName(pillInfo.getEntpName());
                pillResult.setItemName(pillInfo.getItemName());
                pillResult.setEfcyQesitm(pillInfo.getEfcyQesitm());
                pillResult.setUseMethodQesitm(pillInfo.getUseMethodQesitm());
                pillResult.setAtpnWarnQesitm(pillInfo.getAtpnWarnQesitm());
                pillResult.setAtpnQesitm(pillInfo.getAtpnQesitm());
                pillResult.setIntrcQesitm(pillInfo.getIntrcQesitm());
                pillResult.setSeQesitm(pillInfo.getSeQesitm());
                pillResult.setDepositMethodQesitm(pillInfo.getDepositMethodQesitm());
                pillResult.setOpenDe(pillInfo.getOpenDe());
                pillResult.setUpdateDe(pillInfo.getUpdateDe());
                pillResult.setItemImage(pillInfo.getItemImage());
                pillResult.setBizrno(pillInfo.getBizrno());
                pillResult.setPositive(positive);
                pillResult.setNegative(negative);

                dbService.insert(pillResult);

                response.setCode(200);
                response.setMessage("db에 새로 저장됨.");
                response.setSuccess(true);
                response.setPillData(dbService.pillToPillInfo(pillResult));
                response.setGptPositiveTag(pillResult.getPositive());
                response.setGptNegativeTag(pillResult.getNegative());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                logger.error("chatGPT 통신 오류");
                response.setCode(500);
                response.setMessage("chatGPT 통신 오류");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /* image 기반 검색 */
    @PostMapping(value = "/search/image")
    public ResponseEntity<?> searchByImage(@RequestParam("image") MultipartFile image,
                                           @RequestParam("pillShape") String pillShape) throws Exception {
        ResponseJson<PillInfo> response = new ResponseJson<>();
        Pill pill;
        PillInfo pillInfo = new PillInfo();

        /* 파라미터 검사 */
        String shape = pillShape.trim().toLowerCase();

        if (!(shape.equals("circle") ||
                shape.equals("ellipse") ||
                shape.equals("diamond") ||
                shape.equals("triangle") ||
                shape.equals("rectangle") ||
                shape.equals("square") ||
                shape.equals("pentagon") ||
                shape.equals("hexagon") ||
                shape.equals("octagon") ||
                shape.equals("etc"))) {
            logger.error("Shape 파라미터가 존재하지 않습니다.");
            response.setCode(500);
            response.setMessage("shape 파라미터 오류");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /* 이미지 파일 업로드 후 메타 데이터 파일 생성 */
        if (!imageService.makeMeta(shape, image)) {
            logger.error("이미지 파일 업로드에 실패하였습니다.");
            response.setCode(500);
            response.setMessage("이미지 파일 업로드에 실패하였습니다.");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("이미지 저장 후 메타 데이터 파일 생성됨");

        /* 이미지 기반 예측 */
        List<ImageResult> imageResult = imageService.predict();
        
        if (imageResult == null){
            logger.error("이미지 예측에 실패하였습니다.");
            response.setCode(500);
            response.setMessage("이미지 예측에 실패하였습니다.");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        /* 예측 결과 기반 검색 및 저장 */
        try {
            pillInfo = searchService.searchByName(imageResult.get(0).getCode().toString().substring(0, 3));
            logger.info("1순위 : " + imageResult.get(0).getCode().toString());
        } catch (Exception e) {
            logger.error(e.toString());
            response.setCode(500);
            response.setMessage("e약은요 api 통신 오류");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        String itemSeq = pillInfo.getItemSeq();

        if (dbService.findBySeq(itemSeq) != null){ /* db 내에 존재하는 경우 */
            pill = dbService.findBySeq(itemSeq);
            pillInfo = dbService.pillToPillInfo(pill);
            response.setCode(200);
            response.setMessage("db에 이미 저장됨.");
            response.setSuccess(true);
            response.setPillData(pillInfo);
            response.setGptPositiveTag(pill.getPositive());
            response.setGptNegativeTag(pill.getNegative());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            /* GPT 키워드 추출 */
            String positivePrompt = "(1) Extract positive summary keywords from user's input.\n" +
                    "(2) Print five of the most trivial and popular summary keywords in Korean. Each keywords dinstinguished by spacing. \n" +
                    "(3) When you print them, you must not print any explaination and punctuation marks. Only Summary Keywords.\n\n" +
                    pillInfo.getEfcyQesitm() + "\n" +
                    pillInfo.getUseMethodQesitm() + "\n" +
                    pillInfo.getIntrcQesitm() + "\n" +
                    pillInfo.getUseMethodQesitm();

            String negativePrompt = "(1) Extract side effects from user's input.\n" +
                    "(2) Print five of the most trivial and popular side effects in Korean. Each keywords dinstinguished by spacing.\n" +
                    "(3) When you print them, you must not print any explaination and punctuation marks. Only Side Effects.\n\n" +
                    pillInfo.getUseMethodQesitm() + "\n" +
                    pillInfo.getAtpnWarnQesitm() + "\n" +
                    pillInfo.getAtpnQesitm() + "\n" +
                    pillInfo.getIntrcQesitm() + "\n" +
                    pillInfo.getSeQesitm();

            GPTRequest positiveRequest = new GPTRequest(model, positivePrompt);
            GPTResponse positiveResponse = template.postForObject(apiURL, positiveRequest, GPTResponse.class);
            GPTRequest negativeRequest = new GPTRequest(model, negativePrompt);
            GPTResponse negativeResponse = template.postForObject(apiURL, negativeRequest, GPTResponse.class);

            if (positiveResponse != null && negativeResponse != null) {
                String positive = positiveResponse.getChoices().get(0).getMessage().getContent();
                String negative = negativeResponse.getChoices().get(0).getMessage().getContent();

                Pill pillResult = new Pill();
                pillResult.setItemSeq(pillInfo.getItemSeq());
                pillResult.setEntpName(pillInfo.getEntpName());
                pillResult.setItemName(pillInfo.getItemName());
                pillResult.setEfcyQesitm(pillInfo.getEfcyQesitm());
                pillResult.setUseMethodQesitm(pillInfo.getUseMethodQesitm());
                pillResult.setAtpnWarnQesitm(pillInfo.getAtpnWarnQesitm());
                pillResult.setAtpnQesitm(pillInfo.getAtpnQesitm());
                pillResult.setIntrcQesitm(pillInfo.getIntrcQesitm());
                pillResult.setSeQesitm(pillInfo.getSeQesitm());
                pillResult.setDepositMethodQesitm(pillInfo.getDepositMethodQesitm());
                pillResult.setOpenDe(pillInfo.getOpenDe());
                pillResult.setUpdateDe(pillInfo.getUpdateDe());
                pillResult.setItemImage(pillInfo.getItemImage());
                pillResult.setBizrno(pillInfo.getBizrno());
                pillResult.setPositive(positive);
                pillResult.setNegative(negative);

                dbService.insert(pillResult);

                response.setCode(200);
                response.setMessage("db에 새로 저장됨.");
                response.setSuccess(true);
                response.setPillData(dbService.pillToPillInfo(pillResult));
                response.setGptPositiveTag(pillResult.getPositive());
                response.setGptNegativeTag(pillResult.getNegative());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                logger.error("chatGPT 통신 오류");
                response.setCode(500);
                response.setMessage("chatGPT 통신 오류");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
    

    /* 날씨 검색 */
    @GetMapping(value = "/weather")
    public ResponseEntity<?> searchWeather(@RequestParam("base_date") String base_date,
                                           @RequestParam("base_time") String base_time,
                                           @RequestParam("nx") String nx,
                                           @RequestParam("ny") String ny,
                                           @RequestParam("sidoName") String sidoName) throws Exception{
    
        WeatherAPI weather = new WeatherAPI();
        try {
            weather = searchService.searchWeather(base_date, base_time, nx, ny, sidoName);
            weather.setResultCode("200");
            weather.setResultMsg("기온, 강수 형태, 미세먼지, 초미세먼지, 통합대기환경지수");
            return new ResponseEntity<>(weather, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.toString());
            weather.setResultCode("500");
            weather.setResultMsg("날씨 관련 api 통신 중 에러 발생");
            return new ResponseEntity<>(weather, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
