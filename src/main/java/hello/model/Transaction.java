package hello.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Double price;
    private Double cost;
    private Double amount;
    private String type;
    private Date timestamp;
}
