package hello;


import static hello.ApiController.BASE_URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

import hello.dao.EtherDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MarketDataService {

    @Autowired
    private EtherDao etherDao;

    public void storeEtherData() {
        etherDao.insertPrice(queryEtherPrice());
    }

    public Double queryEtherPrice() {
        GetRequest httpReq = Unirest.get(BASE_URI + "/products/ETH-EUR/ticker")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
        HttpResponse<JsonNode> response = null;
        try {
            response = httpReq.asJson();
        } catch (UnirestException e) {
            throw new RuntimeException("Failed to query ether price", e);
        }
        return Double.valueOf((String) response.getBody().getObject().get("price"));
    }
}
