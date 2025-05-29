package com.example.jpa.extra;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@RequiredArgsConstructor
@RestController
public class ApiExtraController {

    // Q86
    @GetMapping("/api/extra/pharmacy")
    public String pharmacy(){
        String apiKey = "D3oa9VQ0e0tl8hkpoqTG9kLJWlUB%2FZmHdAZ0OWRPsS8PcY5Ft6PCx7pI2cgFkJS4z4tTs%2BHXcto50WqCrtlCPQ%3D%3D";
        String url = "https://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown?serviceKey=%s&pageNo=1&numOfRows=10";

        String apiResult = "";

        try {
            URI uri = new URI(String.format(url, apiKey));
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String result = restTemplate.getForObject(uri, String.class);

            apiResult = result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiResult;
    }
}
