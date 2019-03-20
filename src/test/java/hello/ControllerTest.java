package hello;

import com.mashape.unirest.http.HttpResponse;

import hello.dao.EtherDao;
import hello.dao.TransactionDao;
import hello.model.Balance;
import hello.model.PriceAnalysis;
import hello.model.PriceAtTime;
import hello.model.Transaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MarketDataService marketDataService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private Controller controller;

    @Autowired
    private EtherDao etherDao;

    @Autowired
    private TransactionDao transactionDao;

    @Test
    public void getHello() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Hello World!")));
    }

    @Test
    public void get() throws Exception {
        marketDataService.storeEtherData();
    }

    @Test
    public void getAccounts() throws Exception {
        HttpResponse response = accountService.getAccounts();
        System.out.println("");
    }

    @Test
    public void getOpenOrders() throws Exception {
        HttpResponse response = ordersService.getOpenOrders();
        System.out.println("");
    }

    @Test
    public void doEtherBuyOrder() throws Exception {
        HttpResponse response = ordersService.doEtherBuyOrder(200.0, 0.4);
        System.out.println("");
    }

    @Test
    public void doEtherSellOrder() {
        HttpResponse response = ordersService.doEtherSellOrder(1000.0, 0.1);
        System.out.println("");
    }

    @Test
    public void testLastTransaction() {
        Transaction transaction = transactionDao.getLastTransaction();
        System.out.println("");
    }

    @Test
    public void doGetOpenOrdersCount() {
        int i = ordersService.getOpenOrdersCount();
        System.out.println("");
    }

    @Test
    public void doCancelOrders() {
        HttpResponse response = ordersService.cancelOrders();
        int status = response.getStatus();
        while (status != 200) {
            status = ordersService.cancelOrders().getStatus();
            System.out.println(status);
        }
        System.out.println("");
    }

    @Test
    public void getFills() {
        HttpResponse response = ordersService.getFills();
        System.out.println("");
    }

    @Test
    public void getBalances() {
        Map<String, Balance> balances = accountService.getBalances();
        System.out.println("");
    }

    @Test
    public void getHourlyChanges() throws Exception {
        List<PriceAtTime> list = etherDao.getLastMonthInfo();
        Collections.reverse(list);
        List<PriceAnalysis> hourlyGain = new ArrayList<>();
        Long start = list.get(0).getTimestamp() + 3600000;
        Double totalPrice = 0.0;
        Double counter = 0.0;
        Double maxPrice = 0.0;
        Double minPrice = 10000.0;
        for (PriceAtTime priceAtTime : list) {
            if (maxPrice < priceAtTime.getPrice()) {
                maxPrice = priceAtTime.getPrice();
            }
            if (minPrice > priceAtTime.getPrice()) {
                minPrice = priceAtTime.getPrice();
            }
            counter++;
            if (start > priceAtTime.getTimestamp()) {
                totalPrice += priceAtTime.getPrice();
            } else {
                totalPrice += priceAtTime.getPrice();
                hourlyGain.add(new PriceAnalysis(totalPrice / counter, maxPrice, minPrice, ((maxPrice - minPrice) / minPrice) * 100, priceAtTime.getTimestamp()));
                start = priceAtTime.getTimestamp() + 3600000;
                counter = 0.0;
                totalPrice = 0.0;
                maxPrice = 0.0;
                minPrice = 10000.0;
            }
        }

        Collections.sort(hourlyGain, (a, b) -> b.getDifferencePercentage().compareTo(a.getDifferencePercentage()));

        for (int i = 0; i < 100; i++) {
            System.out.println(hourlyGain.get(i));;
        }

        System.out.println("");
    }

    @Test
    public void testTradingProfitability() throws Exception {
        List<PriceAtTime> list = etherDao.getLastMonthInfo();
        //tõuseb 1%, siis osta ja müü 3% peal
        //viimase tunni keskmisest hinnast -2%, siis osta ja müü +2%
    }
}
