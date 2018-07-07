package hello;

import static hello.NumberUtil.round;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hello.dao.EtherDao;
import hello.dao.TransactionDao;
import hello.model.Transaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduledTasks {

    private static final Double SELL_PERCENTAGE = 1.005; // 0.5%
    private static final Double BUY_PERCENTAGE = 0.995; // 0.5%
    private static final Double REMOVE_FROM_BUY_PRICE = 0.01;
    private static final Double MINIMUM_SELL_PRICE = 300.0;
    private static final Double MAXIMUM_BUY_PRICE = 700.0;
    private static final Double MINIMUM_SELL_AMOUNT = 0.4;

    @Autowired
    private MarketDataService marketDataService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private EtherDao etherDao;

//    @Scheduled(fixedRate = 5000)
//    public void queryMarketData() throws Exception {
//        marketDataService.storeEtherData();
//    }

//    @Scheduled(fixedRate = 10000)
    public void doTrading() {
        if (ordersService.getOpenOrdersCount() > 0) {
            log.info("Open orders is above 0");
            return;
        }

        Double currentPrice = etherDao.getLastPrice();

        Transaction transaction = transactionDao.getLastTransaction();

        if ("sell".equals(transaction.getType()) && etherDao.getLastHourPrice() * BUY_PERCENTAGE > currentPrice) {
            Double price = transaction.getPrice() - REMOVE_FROM_BUY_PRICE;
            if (price > MAXIMUM_BUY_PRICE) {
                log.info("Maximum limits have been exceeded. Price: " + price);
                return;
            }
            Double amount = round(transaction.getCost() / price, 2);
            ordersService.doEtherBuyOrder(price, amount);
            transactionDao.insert(new Transaction(price, price * amount, amount, "buy", new Date()));
            return;
        }

        if ("buy".equals(transaction.getType())) {
            Double price = round(transaction.getPrice() * SELL_PERCENTAGE, 2);
            if (currentPrice > price) {
                price = round(currentPrice * SELL_PERCENTAGE, 2);
            }
            Double amount = transaction.getAmount();
            if (price > MINIMUM_SELL_PRICE && amount > MINIMUM_SELL_AMOUNT) {
                ordersService.doEtherSellOrder(price, amount);
                transactionDao.insert(new Transaction(price, price * amount, amount, "sell", new Date()));
            } else {
                log.info("Minimum limits have been exceeded. Price: " + price + ", amount: " + amount);
            }
        }
    }
}
