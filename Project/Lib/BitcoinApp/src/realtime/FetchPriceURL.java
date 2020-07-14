package realtime;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import price.PriceBTC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FetchPriceURL {
    JSONParser parser;
    URL api;

    public FetchPriceURL(String url) {
        try {
            api = new URL(url); // URL to Parse
            parser = new JSONParser();
        } catch (IOException e) {

        }
    }

    public static double fetchLastClose() {
        return 0.;
    }


    public PriceBTC fetchLivePrice(){
        try {
            URLConnection yc = api.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine = in.readLine();
            JSONObject o = (JSONObject) parser.parse(inputLine);

            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss z", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("France/Paris"));
            String datetimeStr = (String) ((JSONObject) o.get("time")).get("updated");
            Date datetime = formatter.parse(datetimeStr);
            double priceEUR = (Double) ((JSONObject) ((JSONObject) o
                    .get("bpi"))
                    .get("EUR"))
                    .get("rate_float");

            double priceUSD = (Double) ((JSONObject) ((JSONObject) o
                    .get("bpi"))
                    .get("USD"))
                    .get("rate_float");

            double priceGBP = (Double) ((JSONObject) ((JSONObject) o
                    .get("bpi"))
                    .get("GBP"))
                    .get("rate_float");
            in.close();

            return new PriceBTC(datetime, priceEUR, priceUSD, priceGBP);

        } catch (IOException | ParseException | java.text.ParseException e){
            e.printStackTrace();
            return null;
        }
    }

//    public ArrayList<PriceBTC> fetchHistoricalPrice(Date fromDate, Date toDate){
//        ArrayList<PriceBTC> prices = new ArrayList<>();
//
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        fromDate.
//    }


    public static void main(String[] args) {
        FetchPriceURL liveData = new FetchPriceURL("https://api.coindesk.com/v1/bpi/currentprice.json");
        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            PriceBTC price = liveData.fetchLivePrice();
            System.out.println(price.toString());

        }, 0, 1, TimeUnit.MINUTES);
    }


}