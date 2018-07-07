package hello.dao;

import hello.model.PriceAtTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class EtherDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void insertPrice(Double price) {
        jdbcTemplate.update("insert into ether (price, import_timestamp) values (?, ?)", price, new Date());
    }

    public List<Double> getLastTenPrices() {
        return jdbcTemplate.queryForList("select price from ether order by id desc limit 500000", Double.class); //500000 is around last 30 days
    }

    public List<PriceAtTime> getLastMonthInfo() {
        final List<Map<String, Object>> rows = jdbcTemplate.queryForList("select price, extract(epoch from import_timestamp)*1000 as import_timestamp from ether order by id desc limit 500000"); //500000 is around last 30 days
        List<PriceAtTime> prices = new ArrayList<>();
        for (final Map row : rows) {
            prices.add(new PriceAtTime((Double) row.get("price"), ((Double) row.get("import_timestamp")).longValue()));
        }
        return prices;
    }

    public Double getLastHourPrice() {
        return jdbcTemplate.queryForObject("select price from ether where import_timestamp > ? order by id asc limit 1", Double.class,
                new Date(System.currentTimeMillis() - 3600 * 1000));
    }

    public Double getLastPrice() {
        return jdbcTemplate.queryForObject("select price from ether order by id desc limit 1", Double.class);
    }
}
