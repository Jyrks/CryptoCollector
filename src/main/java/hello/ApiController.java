package hello;

import java.io.IOException;
import java.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
public class ApiController {

    @Value("${apiPassphrase}")
    private String apiPassphrase;

    @Value("${apiKey}")
    private String apiKey;

    @Value("${apiSecret}")
    private String apiSecret;

    public static final String BASE_URI = "https://api.pro.coinbase.com";
    private static final Gson GSON = new Gson();

    public HttpResponse doGetRequest(String url) {

        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        String cbAccessSign = generateSignature(url, "GET", "");

        BaseRequest httpReq = Unirest.get(BASE_URI + url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("CB-ACCESS-KEY", apiKey)
                .header("CB-ACCESS-SIGN", cbAccessSign)
                .header("CB-ACCESS-TIMESTAMP", String.valueOf(Instant.now().getEpochSecond()))
                .header("CB-ACCESS-PASSPHRASE", apiPassphrase);

        return doRequest(httpReq);
    }

    public HttpResponse doPostRequest(String url, Object fields) {
        String jsonBody = GSON.toJson(fields);
        String cbAccessSign = generateSignature(url, "POST", jsonBody);

        BaseRequest httpReq = Unirest.post(BASE_URI + url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("CB-ACCESS-KEY", apiKey)
                .header("CB-ACCESS-SIGN", cbAccessSign)
                .header("CB-ACCESS-TIMESTAMP", String.valueOf(Instant.now().getEpochSecond()))
                .header("CB-ACCESS-PASSPHRASE", apiPassphrase)
                .body(jsonBody);

        return doRequest(httpReq);
    }

    public HttpResponse doDeleteRequest(String url) {
        String cbAccessSign = generateSignature(url, "DELETE", "");

        BaseRequest httpReq = Unirest.get(BASE_URI + url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("CB-ACCESS-KEY", apiKey)
                .header("CB-ACCESS-SIGN", cbAccessSign)
                .header("CB-ACCESS-TIMESTAMP", String.valueOf(Instant.now().getEpochSecond()))
                .header("CB-ACCESS-PASSPHRASE", apiPassphrase);

        return doRequest(httpReq);
    }

    private HttpResponse doRequest(BaseRequest httpReq) {
        try {
            HttpResponse response = httpReq.asJson();
            if (response.getStatus() != 200) {
                log.error(httpReq.getHttpRequest().getUrl() + " failed with status: " + response.getStatus() + ". Message: " + response.getBody());
                throw new RuntimeErrorException(new Error(), "Failed to query");
            }
            return response;
        } catch (UnirestException e) {
            log.error(httpReq.getHttpRequest().getUrl() + " failed with error message: " + e.getMessage());
            throw new RuntimeErrorException(new Error(e.getCause()));
        }
    }

    private String generateSignature(String requestPath, String method, String body) {
        try {
            String prehash = Instant.now().getEpochSecond() + method.toUpperCase() + requestPath + body;
            byte[] secretDecoded = java.util.Base64.getDecoder().decode(apiSecret);
            SecretKeySpec keyspec = new SecretKeySpec(secretDecoded, "HmacSHA256");
            Mac sha256 = Mac.getInstance("HmacSHA256");
            sha256.init(keyspec);
            return java.util.Base64.getEncoder().encodeToString(sha256.doFinal(prehash.getBytes()));
        } catch (Exception e) {
            throw new RuntimeErrorException(new Error("Cannot set up authentication headers."));
        }
    }
}
