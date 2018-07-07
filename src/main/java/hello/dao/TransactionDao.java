package hello.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import hello.model.Transaction;

@Repository
public class TransactionDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final BeanPropertyRowMapper<Transaction> mapper = new BeanPropertyRowMapper<>(Transaction.class);

    public Transaction getLastTransaction() {
        return jdbcTemplate.queryForObject("select price, cost, amount, type, timestamp from transaction order by id desc limit 1", mapper);
    }

    public void insert(Transaction t) {
        jdbcTemplate.update("insert into transaction (price, cost, amount, type, date) values (?,?,?,?)", t.getPrice(), t.getCost(), t.getAmount(), t.getType(), t.getTimestamp());
    }
}
