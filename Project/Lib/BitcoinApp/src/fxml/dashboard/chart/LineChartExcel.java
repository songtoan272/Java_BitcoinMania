package fxml.dashboard.chart;

import fxml.dashboard.util.MyTooltip;
import io.Export;
import io.excel.ExcelReader;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import price.ListPriceBTC;
import price.PriceBTC;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LineChartExcel extends LineChart<String, Number> implements Export {
    final int WINDOW_SIZE = 200; //maximum data point can be plotted on the chart
    double lowerBound;
    double upperBound;
    String scale;
    LocalDateTime fromTime;
    LocalDateTime toTime;
    List<PriceBTC> allPrices;


    public LineChartExcel() {
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

        this.setTitle("Excel Data");
        this.setAnimated(true);
        this.setAlternativeColumnFillVisible(false);

        this.upperBound = 0.0;
        this.lowerBound = 0.0;

        ObservableList<Series<String, Number>> data = this.getData();
        Series<String, Number> priceSeries = new Series<>();
        priceSeries.setName("Price in USD");
        Series<String, Number> lowerThresholdSeries = new Series<>();
        lowerThresholdSeries.setName("LowerBound");
        Series<String, Number> upperThresholdSeries = new Series<>();
        upperThresholdSeries.setName("UpperBound");
        //add series to data and add data to fxml.dashboard.chart
        data.addAll(priceSeries, upperThresholdSeries, lowerThresholdSeries);
//        this.changeSourceFile(new File("/home/songtoan272/Documents/Cours/Semestre_5/Java_Bases/BitcoinMania/Project/Dataset/Fichier-Excel.xlsx"));
    }

    private void feedData(List<PriceBTC> prices) {
        if (prices == null) return;
        Series<String, Number> newDataSeries = this.getData().get(0);
        newDataSeries.getData().clear();
        for (PriceBTC p : prices.subList(0, Math.min(WINDOW_SIZE, prices.size()))) {
            Data<String, Number> point = new Data<>(
                    p.getDatetime().toString(),
                    p.getPriceUSD()
            );
            newDataSeries.getData().add(point);
            Tooltip.install(point.getNode(), new MyTooltip(
                    "Date: " + point.getXValue() + "\n" +
                            "Price: " + point.getYValue()
            ));
        }
    }

    private void updateData(){
        List<PriceBTC> prices = ListPriceBTC.byDates(this.allPrices, this.fromTime, this.toTime);
        prices = ListPriceBTC.byScale(prices, this.scale);
        this.feedData(prices);
    }

    public void changeSourceFile(File file){
        System.out.println("Source Excel changed to " + file.getAbsolutePath());
        this.allPrices = ExcelReader.read(file);
        if (this.allPrices == null) return;
        if (this.allPrices.size()==0) return;
        this.scale = "5 MINS";
        this.fromTime = this.allPrices.get(0).getDatetime();
        this.toTime = this.allPrices.get(allPrices.size()-1).getDatetime();
        this.feedData(this.allPrices);
    }

    private void updateThreshold(boolean isUpper){
        Series<String, Number> series = this.getData().get(isUpper ?1:2);
        series.getData().clear();
        int count = 0;
        for (Data<String, Number> d: this.getData().get(0).getData()){
            Data<String, Number> point = new Data<>(d.getXValue(),
                    isUpper ? this.upperBound:this.lowerBound);
            series.getData().add(point);
            point.getNode().setVisible(false);
            if (isUpper && (double) d.getYValue() >= this.upperBound) count++;
            if (!isUpper && (double) d.getYValue() <= this.lowerBound) count++;
        }
        Tooltip.install(series.getNode(), new MyTooltip(
                isUpper? "Cross Upper Threshold: " + count + " times.":
                        "Cross Lower Threshold: " + count + " times."
        ));
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        if (this.lowerBound == lowerBound) return;
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

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        if (this.scale == null) return;
        if (this.scale.equals(scale)) return;
        this.scale = scale;
        this.updateData();
    }

    public LocalDateTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalDateTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalDateTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalDateTime toTime) {
        this.toTime = toTime;
    }

    public LocalDate getMinDate(){
        if (this.allPrices != null){
            return this.allPrices.get(0).getDatetime().toLocalDate();
        }else return null;
    }

    public LocalDate getMaxDate(){
        if (this.allPrices != null){
            return this.allPrices.get(this.allPrices.size()-1).getDatetime().toLocalDate();
        }else return null;
    }

    @Override
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
                sb.append("USD");
                sb.append('\n');
            }
            writer.write(sb.toString());
            System.out.println("done exporting CSV file to "+file);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void exportSQLFile(File file) {
        String table = "table";
        try (PrintWriter writer = new PrintWriter(file)) {
            List<Data<String, Number>> prices = this.getData().get(0).getData();
            StringBuilder sb = new StringBuilder();
            //All insertion line
            for (Data<String, Number> d : prices) {
                sb.append("INSERT INTO " + table + " (date, value, currency) VALUES ('" + d.getXValue() + "', '" + d.getYValue() + "', '" + "USD" + "')");
                sb.append("\n");
            }
            writer.write(sb.toString());
            System.out.println("done exporting SQL file");

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    @Override
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
            contentStreamText.showText("Currency: USD");
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
