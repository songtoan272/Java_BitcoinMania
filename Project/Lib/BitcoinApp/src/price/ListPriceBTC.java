package price;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class ListPriceBTC {
    private static LocalDateTime addScale(LocalDateTime start, String scale){
        return switch (scale){
//            case "5 MINS" -> start.plusMinutes(5);
            case "15 MINS" -> start.plusMinutes(15);
            case "HOUR" -> start.plusHours(1);
            case "12 HOURS" -> start.plusHours(12);
            case "DAY" -> start.plusDays(1);
            case "3 DAYS" -> start.plusDays(3);
            case "WEEK" -> start.plusWeeks(1);
            case "15 DAYS" -> start.plusDays(15);
            case "MONTH" -> start.plusMonths(1);
            default -> null;
        };
    }
    public static List<PriceBTC> byScale(List<PriceBTC> l, String scale) {
        if (scale.equals("5 MINS")) return l;
        LocalDateTime start = l.get(0).getDatetime();
        LocalDateTime end = l.get(l.size()-1).getDatetime();
        LocalDateTime next = addScale(start, scale);
        next = next.isAfter(end) ? end:next;
        List<PriceBTC> res = new LinkedList<>();
        res.add(new PriceBTC(start, l.get(0).getPriceUSD()));
        for (PriceBTC p: l){
            if (p.getDatetime().equals(next)){
                res.add(new PriceBTC(p.getDatetime(), p.getPriceUSD()));
                next = addScale(next, scale);
                next = next.isAfter(end) ? end:next;
            }
        }
        return res;
    }

    public static List<PriceBTC> byDates(List<PriceBTC> l, LocalDateTime from, LocalDateTime to){
        int start = 0;
        int end = 0;
        for (PriceBTC p: l){
            if (p.getDatetime().equals(from)) start = l.indexOf(p);
            if (p.getDatetime().equals(to)){
                end = l.indexOf(p);
                break;
            }
        }
        return l.subList(start, end);
    }
}
