package hello;

import com.mashape.unirest.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private ApiController apiController;

    public HttpResponse getAccounts() {
        return apiController.doGetRequest("/accounts");
    }
}
