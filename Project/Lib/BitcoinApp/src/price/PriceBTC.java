package price;
import java.util.Date;

public class PriceBTC {
    private Date datetime;
    private double priceEUR;
    private double priceUSD;
    private double priceGBP;

    public PriceBTC(Date datetime, double priceEUR, double priceUSD, double priceGBP){
        this.datetime = datetime;
        this.priceEUR = priceEUR;
        this.priceUSD = priceUSD;
        this.priceGBP = priceGBP;
    }

    public Date getDatetime() {
        return datetime;
    }

    public double getPriceEUR() {
        return priceEUR;
    }

    @Override
    public String toString() {
        return "PriceBTC{" +
                "datetime=" + datetime +
                ", priceEUR=" + priceEUR +
                ", priceUSD=" + priceUSD +
                ", priceGBP=" + priceGBP +
                '}';
    }

    public double getPriceUSD() {
        return priceUSD;
    }

    public double getPriceGBP() {
        return priceGBP;
    }

}
