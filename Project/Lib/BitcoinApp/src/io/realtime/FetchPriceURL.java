package io.realtime;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FetchPriceURL {

    public static double fetchLastClose() {
        try {
            URL api = new URL("https://api.coindesk.com/v1/bpi/historical/close.json");
            JSONParser parser = new JSONParser();
            URLConnection yc = api.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine = in.readLine();
            JSONObject o = (JSONObject) parser.parse(inputLine);
            JSONObject bpi = ((JSONObject) o.get("bpi"));
            LocalDate lastDate = LocalDate.now().minusDays(2);
            double price = ((Double) bpi
                    .get(lastDate.format(DateTimeFormatter.ISO_LOCAL_DATE)));
            in.close();
            return price;

        } catch (IOException | ParseException e){
            e.printStackTrace();
            return 0.0;
        }
    }


    public static PriceBTC fetchLivePrice(){
        try {
            URL api = new URL("https://api.coindesk.com/v1/bpi/currentprice.json"); // URL to Parse
            JSONParser parser = new JSONParser();
            URLConnection yc = api.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine = in.readLine();
            JSONObject o = (JSONObject) parser.parse(inputLine);

            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss z", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("France/Paris"));
            String datetimeStr = (String) ((JSONObject) o.get("time")).get("updated");
            Date datetime = formatter.parse(datetimeStr);

            HashMap<String, Double> prices = new HashMap<>();
            JSONObject bpi = ((JSONObject) o.get("bpi"));
            for (Object currency : bpi.keySet()){
                prices.put(currency.toString(),
                        ((Double) ((JSONObject) bpi
                        .get(currency.toString()))
                        .get("rate_float")));
            }
            return new PriceBTC(LocalDateTime.ofInstant(
                    datetime.toInstant(),
                    ZoneId.systemDefault()), prices);

        } catch (IOException | ParseException | java.text.ParseException e){
            e.printStackTrace();
            return null;
        }
    }x`

    private static List<PriceBTC> fetchHistoricalPrice(String url){
        LinkedList<PriceBTC> prices = new LinkedList<>();
        try {
            URL api = new URL(url);
            JSONParser parser = new JSONParser();
            URLConnection yc = api.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine = in.readLine();
            JSONObject o = (JSONObject) parser.parse(inputLine);
            JSONObject bpi = ((JSONObject) o.get("bpi"));
            LocalDate toDate = LocalDate.now();
            LocalDate fromDate = toDate.minusDays(31);
            for (LocalDate d=fromDate; d.isBefore(toDate); d=d.plusDays(1)){
                Double price;
                try{
                    price = ((Double) bpi
                            .get(d.format(DateTimeFormatter.ISO_LOCAL_DATE)));
                }catch (java.lang.ClassCastException e) {
                    price = ((Long) bpi
                            .get(d.format(DateTimeFormatter.ISO_LOCAL_DATE))).doubleValue();
                }
//                System.out.println("date="+formatter.format(d) + "price=" + price);
                if (price != null) prices.add(new PriceBTC(d.atStartOfDay(), price));
            }
            in.close();
            Collections.sort(prices);
            return prices;

        } catch (IOException | ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<PriceBTC> fetchHistoricalPrice(LocalDate fromDate, LocalDate toDate){
        if (fromDate == null){
            return fetchHistoricalPrice();
        }
        if (toDate == null){
            toDate = LocalDate.now();
        }
        if (toDate.isBefore(fromDate)){
            return fetchHistoricalPrice();
        }
        if (toDate.isBefore(fromDate) || toDate.equals(fromDate)){
            return fetchHistoricalPrice();
        }
        if (toDate.isAfter(LocalDate.now(ZoneId.of("UTC")))) {
            toDate = LocalDate.now(ZoneId.of("UTC"));
        }
        if (fromDate.isBefore(LocalDate.of(2010, 7, 18))){
            fromDate = LocalDate.of(2010, 7, 18);
        }
        LinkedList<PriceBTC> prices = new LinkedList<>();

        String fromDateStr = fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String toDateStr = toDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String url = "https://api.coindesk.com/v1/bpi/historical/close.json?start=" +
                fromDateStr + "&end=" + toDateStr;
        try {
            URL api = new URL(url);
            JSONParser parser = new JSONParser();
            URLConnection yc = api.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine = in.readLine();
            JSONObject o = (JSONObject) parser.parse(inputLine);
            JSONObject bpi = ((JSONObject) o.get("bpi"));

            for (LocalDate d=fromDate; d.isBefore(toDate); d=d.plusDays(1)){
                Double price;
                try{
                    price = ((Double) bpi
                            .get(d.format(DateTimeFormatter.ISO_LOCAL_DATE)));
                }catch (java.lang.ClassCastException e) {
                    price = ((Long) bpi
                            .get(d.format(DateTimeFormatter.ISO_LOCAL_DATE))).doubleValue();
                }
//                System.out.println("date="+formatter.format(d) + "price=" + price);
                prices.add(new PriceBTC(d.atStartOfDay(), price));
            }
            in.close();
            return prices;

        } catch (IOException | ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<PriceBTC> fetchHistoricalPrice(){
        return fetchHistoricalPrice("https://api.coindesk.com/v1/bpi/historical/close.json");
    }

    public static List<PriceBTC> fetchHistoricalPrice(LocalDate fromDate){
        return fetchHistoricalPrice(fromDate, null);
    }

    public static void main(String[] args) {
        LocalDate startDate = LocalDate.of(2010, 7,10);
        LocalDate endDate = LocalDate.of(2020, 7,17);
        LinkedList<PriceBTC> price = (LinkedList<PriceBTC>) FetchPriceURL.fetchHistoricalPrice(startDate, endDate);
        for (PriceBTC p : price){
            System.out.println("date="+p.getDatetime().toString() + "price=" + p.getPriceUSD());
        }
    }


}