package com.example.yaksokServer.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.yaksokServer.model.PillInfo;
import com.example.yaksokServer.model.WeatherAPI;

import lombok.RequiredArgsConstructor;

/* e약은요 검색 */

@Service
@RequiredArgsConstructor
public class SearchService {
    private static Logger logger = LoggerFactory.getLogger(SearchService.class);

    /* 품목기준코드 기반 검색 */
    public static PillInfo searchBySeq(String itemSeq) throws Exception {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList");
        String apiKey = "api_key";
        StringBuilder sb = new StringBuilder();
        BufferedReader br;

        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + apiKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("itemSeq", "UTF-8") + "=" + URLEncoder.encode(itemSeq, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(sb.toString());
        JSONObject body = (JSONObject) jsonObject.get("body");
        JSONArray item = (JSONArray) body.get("items");
        JSONObject target = (JSONObject) item.get(0);

        PillInfo pillResult = new PillInfo();

        pillResult.setItemSeq(target.get("itemSeq").toString());
        pillResult.setEntpName(target.get("entpName") != null ? target.get("entpName").toString() : "Unknown");
        pillResult.setItemName(target.get("itemName") != null ? target.get("itemName").toString() : "Unknown");
        pillResult.setEfcyQesitm(target.get("efcyQesitm") != null ? target.get("efcyQesitm").toString() : "Unknown");
        pillResult.setUseMethodQesitm(target.get("useMethodQesitm") != null ? target.get("useMethodQesitm").toString() : "Unknown");
        pillResult.setAtpnWarnQesitm(target.get("atpnWarnQesitm") != null ? target.get("atpnWarnQesitm").toString() : "Unknown");
        pillResult.setAtpnQesitm(target.get("atpnQesitm") != null ? target.get("atpnQesitm").toString() : "Unknown");
        pillResult.setIntrcQesitm(target.get("intrcQesitm") != null ? target.get("intrcQesitm").toString() : "Unknown");
        pillResult.setSeQesitm(target.get("seQesitm") != null ? target.get("seQesitm").toString() : "Unknown");
        pillResult.setDepositMethodQesitm(target.get("depositMethodQesitm") != null ? target.get("depositMethodQesitm").toString() : "Unknown");
        pillResult.setOpenDe(target.get("openDe") != null ? target.get("openDe").toString() : "Unknown");
        pillResult.setUpdateDe(target.get("updateDe") != null ? target.get("updateDe").toString() : "Unknown");
        pillResult.setItemImage(target.get("itemImage") != null ? target.get("itemImage").toString() : "Unknown");
        pillResult.setBizrno(target.get("bizrno") != null ? target.get("bizrno").toString() : "Unknown");

        logger.info(pillResult.toString());

        return pillResult;
    }

    /* 이름 기반 검색 */
    public static PillInfo searchByName(String itemName) throws Exception {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList");
        String apiKey = "api_key";
        StringBuilder sb = new StringBuilder();
        BufferedReader br;

        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + apiKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("itemName", "UTF-8") + "=" + URLEncoder.encode(itemName, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(sb.toString());
        JSONObject body = (JSONObject) jsonObject.get("body");
        JSONArray item = (JSONArray) body.get("items");
        if (item == null){
            String temp = "게보린";
            byte[] bytes = temp.getBytes(StandardCharsets.UTF_8);
            String tem = new String (bytes, StandardCharsets.UTF_8);
            return searchByName(tem);
        }
        JSONObject target = (JSONObject) item.get(0);

        PillInfo pillResult = new PillInfo();

        pillResult.setItemSeq(target.get("itemSeq").toString());
        pillResult.setEntpName(target.get("entpName") != null ? target.get("entpName").toString() : "Unknown");
        pillResult.setItemName(target.get("itemName") != null ? target.get("itemName").toString() : "Unknown");
        pillResult.setEfcyQesitm(target.get("efcyQesitm") != null ? target.get("efcyQesitm").toString() : "Unknown");
        pillResult.setUseMethodQesitm(target.get("useMethodQesitm") != null ? target.get("useMethodQesitm").toString() : "Unknown");
        pillResult.setAtpnWarnQesitm(target.get("atpnWarnQesitm") != null ? target.get("atpnWarnQesitm").toString() : "Unknown");
        pillResult.setAtpnQesitm(target.get("atpnQesitm") != null ? target.get("atpnQesitm").toString() : "Unknown");
        pillResult.setIntrcQesitm(target.get("intrcQesitm") != null ? target.get("intrcQesitm").toString() : "Unknown");
        pillResult.setSeQesitm(target.get("seQesitm") != null ? target.get("seQesitm").toString() : "Unknown");
        pillResult.setDepositMethodQesitm(target.get("depositMethodQesitm") != null ? target.get("depositMethodQesitm").toString() : "Unknown");
        pillResult.setOpenDe(target.get("openDe") != null ? target.get("openDe").toString() : "Unknown");
        pillResult.setUpdateDe(target.get("updateDe") != null ? target.get("updateDe").toString() : "Unknown");
        pillResult.setItemImage(target.get("itemImage") != null ? target.get("itemImage").toString() : "Unknown");
        pillResult.setBizrno(target.get("bizrno") != null ? target.get("bizrno").toString() : "Unknown");

        logger.info(pillResult.toString());

        return pillResult;
    }
    
    /* 날씨 검색 */
    public static WeatherAPI searchWeather(String base_date, String base_time, String nx, String ny, String sidoName) throws Exception {
        // 초단기실황
        StringBuilder weatherBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst");
        // 시도별 실시간 측정정보
        StringBuilder airBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty");
        String apiKey = "api_key";
        StringBuilder sb = new StringBuilder();
        BufferedReader br;

        WeatherAPI weather = new WeatherAPI();
        weather.setBaseDate(base_date);
        weather.setBaseTime(base_time);
        weather.setNx(nx);
        weather.setNy(ny);
        weather.setSidoName(sidoName);

        
        /* 기온, 강수 형태 조회 */
        weatherBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + apiKey); /*Service Key*/
        weatherBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        weatherBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        weatherBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8"));
        weatherBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(base_date, "UTF-8"));
        weatherBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(base_time, "UTF-8"));
        weatherBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
        weatherBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));

        URL weatherUrl = new URL(weatherBuilder.toString());
        HttpURLConnection weatherConn = (HttpURLConnection) weatherUrl.openConnection();
        weatherConn.setRequestMethod("GET");
        weatherConn.setRequestProperty("Content-type", "application/json");

        br = new BufferedReader(new InputStreamReader(weatherConn.getInputStream(), "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(sb.toString());
        JSONObject response = (JSONObject) jsonObject.get("response");
        JSONObject header = (JSONObject) response.get("header");
        JSONObject body = (JSONObject) response.get("body");
        JSONObject items = (JSONObject) body.get("items");
        JSONArray item = (JSONArray) items.get("item");
            
        for (int i=0; i<8; i++){
            JSONObject target = (JSONObject) item.get(i);
            if (target.get("category").toString().equals("T1H"))
                weather.setT1H(target.get("obsrValue").toString());
            else if (target.get("category").toString().equals("PTY"))
                weather.setPTY(target.get("obsrValue").toString());
        }
        
        /* 미세먼지, 초미세먼지, 통합대기환경지수 조회 */
        airBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + apiKey); /*Service Key*/
        airBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
        airBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        airBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        airBuilder.append("&" + URLEncoder.encode("sidoName","UTF-8") + "=" + URLEncoder.encode(sidoName, "UTF-8"));
        airBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.0", "UTF-8"));

        URL airUrl = new URL(airBuilder.toString());
        HttpURLConnection airConn = (HttpURLConnection) airUrl.openConnection();
        airConn.setRequestMethod("GET");
        airConn.setRequestProperty("Content-type", "application/json");

        br = new BufferedReader(new InputStreamReader(airConn.getInputStream(), "UTF-8"));
        sb = new StringBuilder();
        String temp;
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        br.close();

        jsonParser = new JSONParser();
        jsonObject = (JSONObject) jsonParser.parse(sb.toString());
        response = (JSONObject) jsonObject.get("response");
        header = (JSONObject) response.get("header");
        body = (JSONObject) response.get("body");
        item = (JSONArray) body.get("items");
            
        JSONObject target = (JSONObject) item.get(0);
        weather.setPm10(target.get("pm10Value").toString());
        weather.setPm25(target.get("pm25Value").toString());
        weather.setKhaiGrade(target.get("khaiGrade").toString());

        return weather;
    }
}
