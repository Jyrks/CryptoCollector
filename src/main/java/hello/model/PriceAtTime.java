package hello.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceAtTime {
    private Double price;
    private Long timestamp;
}
