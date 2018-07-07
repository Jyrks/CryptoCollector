package hello;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import hello.model.PostOrder;

@Service
public class OrdersService {

    @Autowired
    private ApiController apiController;

    public HttpResponse getOpenOrders() {
        return apiController.doGetRequest("/orders");
    }

    public int getOpenOrdersCount() {
        HttpResponse response = apiController.doGetRequest("/orders");
        JSONArray array = ((JsonNode)response.getBody()).getArray();
        return array.length();
    }

    public HttpResponse doEtherBuyOrder(Double price, Double size) {
        PostOrder postOrder = new PostOrder();
        postOrder.setSize(String.valueOf(size));
        postOrder.setPrice(String.valueOf(price));
        postOrder.setSide("buy");
        postOrder.setProductId("ETH-EUR");
        postOrder.setType("limit");
        postOrder.setPostOnly(true);
        postOrder.setCancelAfter("hour");
        postOrder.setTimeInForce("GTT");

        return apiController.doPostRequest("/orders", postOrder);
    }

    public HttpResponse doEtherSellOrder(Double price, Double size) {
        PostOrder postOrder = new PostOrder();
        postOrder.setSize(String.valueOf(size));
        postOrder.setPrice(String.valueOf(price));
        postOrder.setSide("sell");
        postOrder.setProductId("ETH-EUR");
        postOrder.setType("limit");
        postOrder.setPostOnly(true);

        return apiController.doPostRequest("/orders", postOrder);
    }

    public HttpResponse cancelOrders() {
        return apiController.doDeleteRequest("/orders");
    }
}
