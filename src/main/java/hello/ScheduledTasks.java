package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private MarketDataService marketDataService;

    @Scheduled(fixedRate = 5000)
    public void QueryMarketData() throws Exception {
        marketDataService.storeEtherData();
    }
}
