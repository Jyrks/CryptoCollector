package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class EtherDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void insertPrice(Double price) {
        jdbcTemplate.update("insert into ether (price, import_timestamp) values (?, ?)", price, new Date());
    }
}
