package price;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

public class PriceBTC implements Comparable<PriceBTC>{
    private final LocalDateTime datetime;
    private final HashMap<String,  Double> price;

    public PriceBTC(LocalDateTime datetime, HashMap<String, Double> price){
        this.datetime = datetime;
        this.price = new HashMap<>(price.size());
        for (String k : price.keySet()){
            this.price.put(k, price.get(k));
        }
    }

    public PriceBTC(LocalDateTime datetime, double priceUSD){
        this.datetime = datetime;
        this.price = new HashMap<>(1);
        this.price.put("USD", priceUSD);
    }

    public PriceBTC(Date datetime, double priceUSD){
        this.datetime = LocalDateTime.ofInstant(datetime.toInstant(), ZoneId.systemDefault());
        this.price = new HashMap<>(1);
        this.price.put("USD", priceUSD);
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public double getPriceUSD() {
        return price.get("USD");
    }

    public double getPriceGBP() {
        return price.get("GBP");
    }

    public double getPriceEUR() {
        return price.get("EUR");
    }

    public double getPrice(String currency){
        if (!price.keySet().contains(currency)) return -1.0;
        return price.get(currency);
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer("PriceBTC{" +
                "datetime=" + datetime +
                ", price=");
//        for (String c : price.keySet()){
//            res.append("price"+c+"="+this.getPrice(c));
//        }
        res.append(price.toString() + "}");
        return res.toString();
    }

    public boolean before(PriceBTC other){
        return this.datetime.isBefore(other.getDatetime());
    }

    public boolean after(PriceBTC other){
        return this.datetime.isAfter(other.getDatetime());
    }

    @Override
    public int compareTo(@NotNull PriceBTC o) {
        return this.datetime.compareTo(o.getDatetime());
    }
}
