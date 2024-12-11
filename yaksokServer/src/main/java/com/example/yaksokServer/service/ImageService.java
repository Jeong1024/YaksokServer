package com.example.yaksokServer.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.yaksokServer.dto.Metadata;
import com.example.yaksokServer.model.ImageResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {
    private static Logger logger = (Logger) LoggerFactory.getLogger(ImageService.class);

    private static List file_path;

    private static String uploadPath = "/home/ubuntu/YAKSOKSERVER/yaksokServer/src/main/java/com/example/yaksokServer/image/temp/";
    private static String pythonPath = "/usr/bin/python3";
    private static String codePath = "/home/ubuntu/YAKSOKSERVER/predict/PillMain.py";

    /* 파일 업로드 기능 */
    public static boolean upload(byte[] bytea, String target) {
        try {
            File file = new File(target);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytea);
            fos.close();

        } catch (IOException e) {
            logger.error("IOException");
            return false;
        }
        return true;
    }

    /* 문자열 추출 json -> list, ImageResult 형식 */
    public static List<ImageResult> convert(String jsonString) throws IOException {
        List<ImageResult> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        result = mapper.readValue(jsonString, mapper.getTypeFactory().constructCollectionType(ArrayList.class, ImageResult.class));

        return result;
    }

    /* 파일 저장 */
    public static boolean save(String content, String target) {
        byte[] bytea = content.getBytes();
        try {
            File file = new File(target);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytea);
            fos.close();

        } catch (IOException e) {
            logger.error("IO Exception");
            return false;
        }
        return true;
    }

    /* 명령어 실행 */
    public static String execute(String command) {
        StringBuilder result = new StringBuilder();
        Process p;

        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        String res = result.toString();
        logger.info("명령어 실행 = " + command);
        logger.info("실행 결과 : " + res);
        return res;
    }

    // 이미지 파일 업로드 후 메타 데이터 파일 생성
    public static boolean makeMeta(String shape, MultipartFile image) throws Exception {

        file_path = new ArrayList();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        shape = shape.trim().toLowerCase();

        /* 이미지 파일 업로드 */
        StringBuilder fileName = new StringBuilder();
        fileName.append(shape);
        fileName.append("_");
        fileName.append(dateFormat.format(now));
        fileName.append("_.jpg");

        file_path.add(fileName.toString());

        if (upload(image.getBytes(), uploadPath + fileName.toString()) == false) {
            logger.error("이미지 파일 업로드에 실패하였습니다." + uploadPath + fileName.toString());
            return false;
        }
        logger.info("이미지 파일 저장 완료");


        /* 메타 데이터 파일 생성 */
        Metadata metadata = new Metadata();
        metadata.setShape(shape);
        metadata.setDrug_code("none");

        fileName = new StringBuilder();
        fileName.append(shape);
        fileName.append("_");
        fileName.append(dateFormat.format(now));
        fileName.append(".csv");

        file_path.add(fileName.toString());

        if (save(metadata.toString(), uploadPath + fileName.toString()) == false) {
            logger.error("메타데이터 파일 저장에 실패하였습니다.");
            return false;
        }
        logger.info("메타데이터 파일 생성 완료");

        return true;
    }

    /* 생성된 메타파일 기반 이미지 예측 */
    public static List<ImageResult> predict() throws Exception {
        StringBuffer command = new StringBuffer();
        String jsonData;
        List<ImageResult> result;

        command.append(pythonPath);
        command.append(" ");
        command.append(codePath);
        command.append(" ");
        command.append(uploadPath + file_path.get(0)); // ImgPath
        command.append(" ");
        command.append(uploadPath + file_path.get(1)); // CsvPath

        jsonData = execute(command.toString());

        try {
            result = convert(jsonData);
        } catch (IOException | NullPointerException e) {
            logger.error("JSON 형 반환을 실패하였습니다.");
            result = null;
            return result;
        }

        return result;
    }

}
