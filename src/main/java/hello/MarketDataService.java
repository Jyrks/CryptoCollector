package hello;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class MarketDataService {

    public static final String etherDataUrl = "https://api.gdax.com/products/ETH-EUR/ticker";

    @Autowired
    private EtherDAO etherDAO;

    public void storeEtherData() throws UnirestException {
        GetRequest httpReq = Unirest.get(etherDataUrl)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
        Long before = new Date().getTime();
        HttpResponse<JsonNode> response = httpReq.asJson();
        Double price = Double.valueOf((String) response.getBody().getObject().get("price"));
        Long after = new Date().getTime();
        log.info("Logged ether price: " + price + " Query took " + (after -before) + "ms");

        etherDAO.insertPrice(price);
    }
}
