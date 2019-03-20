package hello;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;

import hello.model.Balance;

@Service
public class AccountService {

    @Autowired
    private ApiController apiController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public HttpResponse getAccounts() {
        return apiController.doGetRequest("/accounts");
    }

    public Map<String, Balance> getBalances() {
        List<Balance> balances;
        String response = String.valueOf(apiController.doGetRequest("/accounts").getBody());
        try {
            balances = objectMapper.readValue(response, objectMapper.getTypeFactory().constructCollectionType(List.class, Balance.class));
            return balances.stream().collect(Collectors.toMap(Balance::getCurrency, c -> c));
        } catch (IOException e) {
            throw new RuntimeException("Failed to query balances", e);
        }
    }
}
