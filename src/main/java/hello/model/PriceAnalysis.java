package hello.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceAnalysis {
    private Double averagePrice;
    private Double maxPrice;
    private Double minPrice;
    private Double differencePercentage;
    private Long timestamp;

    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return "PriceAnalysis{" +
                "" + formatter.format(averagePrice) +
                ", " + formatter.format(maxPrice) +
                ", " + formatter.format(minPrice) +
                ", " + formatter.format(differencePercentage) +
                '}';
    }
}
