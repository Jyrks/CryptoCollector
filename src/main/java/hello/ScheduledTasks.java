package hello;

import static hello.NumberUtil.round;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hello.dao.EtherDao;
import hello.dao.TransactionDao;
import hello.model.Balance;
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
    private static final Double MAXIMUM_SELL_AMOUNT = 0.4;

    @Autowired private MarketDataService marketDataService;
    @Autowired private OrdersService ordersService;
    @Autowired private TransactionDao transactionDao;
    @Autowired private EtherDao etherDao;
    @Autowired private AccountService accountService;

    @Scheduled(fixedRate = 5000)
    public void queryMarketData() {
        marketDataService.storeEtherData();
    }

    @Scheduled(fixedRate = 10000)
    public void doTrading() {
        if (ordersService.getOpenOrdersCount() > 0) {
            log.info("Open orders is above 0");
            return;
        }

        Double currentPrice = etherDao.getLastPrice();

        Transaction transaction = transactionDao.getLastTransaction();

        double lastHourPrice = etherDao.getLastHourPrice();
        log.info("Last hour price: " + lastHourPrice);
        log.info("Desired to buy price - minus 0.5%: " + lastHourPrice * BUY_PERCENTAGE);
        log.info("Current price:  " + currentPrice);
        if ("sell".equals(transaction.getType()) && lastHourPrice * BUY_PERCENTAGE > currentPrice) {
            Double price = currentPrice - REMOVE_FROM_BUY_PRICE;
            log.info("Trying to buy with price " + price);
            if (price > MAXIMUM_BUY_PRICE) {
                log.info("Maximum limits have been exceeded. Price: " + price);
                return;
            }
            Double amount = round(transaction.getCost() / price, 2);
            log.info("Buying amount " + amount);
            ordersService.doEtherBuyOrder(price, amount);
            Map<String, Balance> balances = accountService.getBalances();
            transactionDao.insert(new Transaction(price, price * amount, amount, "buy", new Date(), balances.get("EUR").getBalance(), balances.get("ETH").getBalance()));
            return;
        }

        if ("buy".equals(transaction.getType())) {
            Double price = round(transaction.getPrice() * SELL_PERCENTAGE, 2);
            log.info("Trying to sell with price " + price);
            if (currentPrice > price) {
                price = round(currentPrice * SELL_PERCENTAGE, 2);
            }
            Double amount = transaction.getAmount();
            log.info("Selling amount " + amount);
            if (price > MINIMUM_SELL_PRICE && amount < MAXIMUM_SELL_AMOUNT) {
                ordersService.doEtherSellOrder(price, amount);
                Map<String, Balance> balances = accountService.getBalances();
                transactionDao.insert(new Transaction(price, price * amount, amount, "sell", new Date(), balances.get("EUR").getBalance(), balances.get("ETH").getBalance()));
            } else {
                log.info("Minimum limits have been exceeded. Price: " + price + ", amount: " + amount);
            }
        }
    }
}
