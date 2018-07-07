package hello;

import static hello.ApiController.BASE_URI;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;
import java.time.Instant;

@RestController
public class Controller {

    @Autowired
    private ApiController apiAuthentication;

    @RequestMapping("/accounts")
    public HttpResponse getAccounts() throws Exception{
        String url = "/accounts";
        String cbAccessSign = generateSignature(url, "GET", "");

        GetRequest httpReq = Unirest.get(BASE_URI + url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("CB-ACCESS-KEY", apiAuthentication.getApiKey())
                .header("CB-ACCESS-SIGN", cbAccessSign)
                .header("CB-ACCESS-TIMESTAMP", String.valueOf(Instant.now().getEpochSecond()))
                .header("CB-ACCESS-PASSPHRASE", apiAuthentication.getApiPassphrase());
//                    .header("accept", "application/json")
//                    .queryString("apiKey", "123")
//                    .field("parameter", "value")
        HttpResponse<JsonNode> jsonResponse = httpReq.asJson();

        return jsonResponse;
    }

    public String generateSignature(String requestPath, String method, String body) {
        try {
            String prehash = Instant.now().getEpochSecond() + method.toUpperCase() + requestPath + body;
            byte[] secretDecoded = java.util.Base64.getDecoder().decode(apiAuthentication.getApiSecret());
            SecretKeySpec keyspec = new SecretKeySpec(secretDecoded, "HmacSHA256");
            Mac sha256 = Mac.getInstance("HmacSHA256");
            sha256.init(keyspec);
            return java.util.Base64.getEncoder().encodeToString(sha256.doFinal(prehash.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeErrorException(new Error("Cannot set up authentication headers."));
        }
    }
}
