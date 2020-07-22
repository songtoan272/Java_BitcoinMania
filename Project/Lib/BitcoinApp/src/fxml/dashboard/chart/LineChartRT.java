package fxml.dashboard.chart;

import fxml.dashboard.util.AlertBox;
import fxml.dashboard.util.MyTooltip;
import io.Export;
import io.realtime.FetchPriceURL;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import price.PriceBTC;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class LineChartRT extends LineChart<String, Number> implements Export {

    final int WINDOW_SIZE = 100;
    private ScheduledExecutorService scheduledExecutorService;

    private final Series<String, Number> liveDataSeries;
    private final LinkedList<PriceBTC> allPrices;

    private String currency;
    private double lowerBound;
    private double upperBound;
    private double lastClose;
    BooleanProperty overUpper;
    BooleanProperty belowLower;

    public LineChartRT() {
        super(new CategoryAxis(), new NumberAxis());
        CategoryAxis xAxis = (CategoryAxis)  this.getXAxis();
        NumberAxis yAxis = (NumberAxis)  this.getYAxis();

        //defining the axes
//        xAxis.setLabel("Date");
        xAxis.setAnimated(true);
        xAxis.setAutoRanging(true);
        xAxis.setVisible(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);

        yAxis.setAnimated(true);
        yAxis.setAutoRanging(true);
        yAxis.setVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setForceZeroInRange(false);
        yAxis.setTickLabelsVisible(true);
        yAxis.setMinorTickVisible(false);
//        yAxis.set

        //declare attributes
        currency = "USD";
        lowerBound = 0.0;
        upperBound = 0.0;
        liveDataSeries = new Series<>();
        liveDataSeries.setName("Price in "+currency);
        allPrices = new LinkedList<>();
        lastClose = FetchPriceURL.fetchLastClose();
        overUpper = new SimpleBooleanProperty(this, "overUpper", false);
        belowLower = new SimpleBooleanProperty(this, "belowLower", false);
        overUpper.addListener((observableValue, oldVal, newVal) -> {
            if (!oldVal && newVal) this.alert(true);
        });
        belowLower.addListener((observableValue, oldVal, newVal) -> {
            if (!oldVal && newVal) this.alert(false);
        });

        //creating the line fxml.dashboard.chart with two axis created above
        this.setTitle("Bitcoin Price LIVE");
        this.setAnimated(true); // disable animations
        this.setLegendVisible(true);
        this.setAlternativeColumnFillVisible(true);
        this.updateData();
    }

    private void updateData(){
        ObservableList<Series<String, Number>> data = this.getData();
        Series<String, Number> lowerThresholdSeries = new Series<>();
        lowerThresholdSeries.setName("LowerBound");
        Series<String, Number> upperThresholdSeries = new Series<>();
        upperThresholdSeries.setName("UpperBound");
        //add series to data and add data to fxml.dashboard.chart
        data.addAll(this.liveDataSeries, upperThresholdSeries, lowerThresholdSeries);

        Tooltip.install(lowerThresholdSeries.getNode(), new MyTooltip(
                "Lower Threshold: " + lowerBound
        ));
        Tooltip.install(upperThresholdSeries.getNode(), new MyTooltip(
                "Upper Threshold: " + upperBound
        ));

        // setup a scheduled executor to periodically put data into the fxml.dashboard.chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // Update the fxml.dashboard.chart
            Platform.runLater(() -> {
                // fetch new data input from the stream and put it on graph
                PriceBTC price = FetchPriceURL.fetchLivePrice();
                assert price != null;
                System.out.println(price.toString());
                allPrices.add(price);
                Data<String, Number> pricePoint = switch (currency) {
                    case "USD" -> new Data<>(price.getDatetime().toString(), price.getPriceUSD());
                    case "EUR" -> new Data<>(price.getDatetime().toString(), price.getPriceEUR());
                    case "GBP" -> new Data<>(price.getDatetime().toString(), price.getPriceGBP());
                    default -> new Data<>(price.getDatetime().toString(), price.getPriceUSD());
                };
                liveDataSeries.getData().add(pricePoint);
                checkBounds(price.getPrice(currency));
                //set tooltip for the new data point of the graph
                Tooltip.install(pricePoint.getNode(),
                        new MyTooltip("Date: " + pricePoint.getXValue() +"\n" +
                                "Price: " + pricePoint.getYValue() + " " + currency + "\n" +
                                tendency()));

                if (lowerBound > 0.0){
                    Data<String, Number> lowerPoint = new Data<>(price.getDatetime().toString(), lowerBound);
                    lowerThresholdSeries.getData().add(lowerPoint);
                    lowerPoint.getNode().setVisible(false);
                }
                if (upperBound > 0.0){
                    XYChart.Data<String, Number> upperPoint = new Data<>(price.getDatetime().toString(), upperBound);
                    upperThresholdSeries.getData().add(upperPoint);
                    upperPoint.getNode().setVisible(false);
                }

                //Crop graph in window size
                if (liveDataSeries.getData().size() > WINDOW_SIZE) {
                    liveDataSeries.getData().remove(0);
                    lowerThresholdSeries.getData().remove(0);
                    upperThresholdSeries.getData().remove(0);
                }
            });
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void stopUpdate(){
        scheduledExecutorService.shutdownNow();
        scheduledExecutorService.shutdown();
    }


    private String tendency(){
        double currentPrice = liveDataSeries.getData()
                .get(liveDataSeries.getData().size()-1)
                .getYValue().doubleValue();
        if (currentPrice < lastClose){
            double percentage = (lastClose - currentPrice) / lastClose * 100.0;
            return String.format("v%.2f", percentage) + "%";
        }else{
            double percentage = (currentPrice - lastClose) / lastClose * 100.0;
            return String.format("^%.2f", percentage) + "%";
        }
    }

    private void switchCurrencyLive(){
        liveDataSeries.getData().clear();
        XYChart.Data<String, Number> pricePoint;
        for (PriceBTC p : allPrices){
            pricePoint = switch (currency) {
                case "USD" -> new XYChart.Data<>(p.getDatetime().toString(), p.getPriceUSD());
                case "EUR" -> new XYChart.Data<>(p.getDatetime().toString(), p.getPriceEUR());
                case "GBP" -> new XYChart.Data<>(p.getDatetime().toString(), p.getPriceGBP());
                default -> new XYChart.Data<>(p.getDatetime().toString(), p.getPriceUSD());
            };
            liveDataSeries.getData().add(pricePoint);
            Tooltip.install(pricePoint.getNode(),
                    new MyTooltip("Date: " + pricePoint.getXValue() +"\n" +
                            "Price: " + pricePoint.getYValue() + " " + currency + "\n" +
                            tendency()));
        }
    }

    private void updateThreshold(boolean isUpper){
        Series<String, Number> series = this.getData().get(isUpper ?1:2);
        series.getData().clear();
        for (Data<String, Number> d: liveDataSeries.getData()){
            Data<String, Number> point = new Data<>(d.getXValue(),
                    isUpper ? this.upperBound:this.lowerBound);
            series.getData().add(point);
            point.getNode().setVisible(false);
        }
        Tooltip.install(series.getNode(), new MyTooltip(
                isUpper? "Upper Threshold: " + this.upperBound:
                        "Lower Threshold: " + this.lowerBound
        ));
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        if (!this.currency.equals(currency)){
            this.currency = currency;
            this.switchCurrencyLive();
        }
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        if (this.lowerBound == lowerBound){
            return;
        }
        this.lowerBound = lowerBound;
        this.updateThreshold(false);
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        if (this.upperBound == upperBound) return;
        this.upperBound = upperBound;
        this.updateThreshold(true);
    }

    public double getLastClose() {
        return lastClose;
    }

    private void setLastClose(double lastClose) {
        this.lastClose = lastClose;
    }

    private void checkBounds(double lastPrice){
        if  (upperBound > 0.0){
            if (lastPrice >= upperBound && !overUpper.get()){
                overUpper.setValue(true);
            }else if (lastPrice <= upperBound && overUpper.get()){
                overUpper.setValue(false);
            }
        }
        if (lowerBound > 0.0){
            if (lastPrice <= lowerBound && !belowLower.get()){
                belowLower.set(true);
            }else if (lastPrice >= lowerBound && belowLower.get()){
                belowLower.set(false);
            }
        }
    }

    private void alert(boolean isUpper){
        String title = "Threshold Passed!!!";
        String message;
        if (isUpper){
            message = "The price has gone over the upperbound.\n" +
                    "Upperbound = " + this.upperBound + "\n" +
                    "Current Price = " + this.liveDataSeries.getData().get(
                            this.liveDataSeries.getData().size()-1).getYValue() + this.currency;
        }else{
            message = "The price has gone below the lowerbound.\n" +
                    "Lowerbound = " + this.upperBound + "\n" +
                    "Current Price = " + this.liveDataSeries.getData().get(0).getYValue() + this.currency;
        }
        AlertBox.display(title, message);
    }

    public void exportCSVFile(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            List<Data<String, Number>> prices = this.getData().get(0).getData();
            StringBuilder sb = new StringBuilder();
            sb.append("date");
            sb.append(',');
            sb.append("value");
            sb.append(',');
            sb.append("currency");
            sb.append('\n');

            for (Data<String, Number> d: prices){
                sb.append(d.getXValue());
                sb.append(',');
                sb.append(d.getYValue());
                sb.append(',');
                sb.append(currency);
                sb.append('\n');
            }
            writer.write(sb.toString());
            System.out.println("done exporting CSV file to "+file);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void exportSQLFile(File file) {
        String table = "table";
        try (PrintWriter writer = new PrintWriter(file)) {
            List<Data<String, Number>> prices = this.getData().get(0).getData();
            StringBuilder sb = new StringBuilder();
            //All insertion line
            for (Data<String, Number> d : prices) {
                sb.append("INSERT INTO " + table + " (date, value, currency) VALUES ('" + d.getXValue() + "', '" + d.getYValue() + "', '" + this.currency + "')");
                sb.append("\n");
            }
            writer.write(sb.toString());
            System.out.println("done exporting SQL file");

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    public void exportPDFFile(File file){
        List<Data<String, Number>> prices = this.getData().get(0).getData();
        //Open PDF
        try (PDDocument doc = new PDDocument()) {
            PDPage page1 = new PDPage();
            doc.addPage(page1);
            PDPageContentStream contentStreamImg = new PDPageContentStream(doc, page1, true, true);
            //save image of graph to the same folder as the PDF file
            WritableImage chartImg = this.snapshot(new SnapshotParameters(), null);
            File imgFile = new File(file.getParent() +
                    "/savedChart_" + LocalDate.now().toString() + ".png");
            ImageIO.write(SwingFXUtils.fromFXImage(chartImg, null), "png", imgFile);
            //write saved image to PDF file
            PDImageXObject pdImage = PDImageXObject.createFromFile(imgFile.getAbsolutePath(), doc);
            contentStreamImg.drawImage(pdImage, 0, 0,
                    (float) chartImg.getWidth()/2, (float) chartImg.getHeight()/2);
            contentStreamImg.close();

            //write text to PDF
            PDPage page2 = new PDPage();
            doc.addPage(page2);
            PDPageContentStream contentStreamText = new PDPageContentStream(doc, page2, true, true);
            contentStreamText.beginText();
            contentStreamText.setFont(PDType1Font.TIMES_ROMAN, 12);
            contentStreamText.setLeading(14.5f);
            contentStreamText.newLineAtOffset(25, 725);
            contentStreamText.showText("Currency: "+currency);
            contentStreamText.newLine();
            contentStreamText.showText("DATE                     | PRICE ");
            contentStreamText.newLine();
            // Write data to PDF
            for (Data<String, Number> d: prices){
                contentStreamText.showText(d.getXValue() + " | " + d.getYValue());
                contentStreamText.newLine();
            }
            contentStreamText.endText();
            contentStreamText.close();

            doc.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
