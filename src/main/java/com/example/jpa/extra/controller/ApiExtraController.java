package com.example.jpa.extra.controller;

import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.extra.model.OpenApiResult;
import com.example.jpa.extra.model.PharmacySearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ApiExtraController {

    // Q86~88
    @GetMapping("/api/extra/pharmacy")
    public ResponseEntity<?> pharmacy(@RequestBody PharmacySearch pharmacySearch) {
        String apiKey = "D3oa9VQ0e0tl8hkpoqTG9kLJWlUB%2FZmHdAZ0OWRPsS8PcY5Ft6PCx7pI2cgFkJS4z4tTs%2BHXcto50WqCrtlCPQ%3D%3D";
        String url = String.format("https://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown?serviceKey=%s&pageNo=1&numOfRows=10", apiKey);

        String apiResult = "";

        try {
            url += String.format("&Q0=%s&Q1=%s"
                    , URLEncoder.encode(pharmacySearch.getSearchSido(), "UTF-8")
                    , URLEncoder.encode(pharmacySearch.getSearchGugun(), "UTF-8"));

            URI uri = new URI(url);

            log.info(url);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String result = restTemplate.getForObject(uri, String.class);

            apiResult = result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        OpenApiResult jsonResult = null;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            jsonResult = objectMapper.readValue(apiResult, OpenApiResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseResult.success(jsonResult);
    }

}
