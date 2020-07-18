package fxml.dashboard.chart;

import fxml.util.MyTooltip;
import io.Export;
import io.realtime.FetchPriceURL;
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
import price.ListPriceBTC;
import price.PriceBTC;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class LineChartHistorical extends LineChart<String, Number> implements Export {
    private String scale;
    private LocalDate startDate;
    private LocalDate endDate;
    private double lowerBound;
    private double upperBound;
    private XYChart.Series<String, Number> dataSeries;

    public LineChartHistorical() {
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

        this.setTitle("Historical Data");
        this.setAnimated(true);
        this.setAlternativeColumnFillVisible(true);

        upperBound = 0.0;
        lowerBound = 0.0;
        scale = "DAY";
        endDate = LocalDate.now();
        startDate = endDate.minusDays(31);
        dataSeries = new XYChart.Series<>();
        dataSeries.setName("Price in USD");
        Series<String, Number> lowerThresholdSeries = new Series<>();
        lowerThresholdSeries.setName("LowerBound");
        Series<String, Number> upperThresholdSeries = new Series<>();
        upperThresholdSeries.setName("UpperBound");
        this.getData().addAll(this.dataSeries, upperThresholdSeries, lowerThresholdSeries);
        this.feedData(FetchPriceURL.fetchHistoricalPrice());
    }

    private void feedData(List<PriceBTC> prices){
        if (prices==null) return;
        Series<String, Number> newSeries = this.getData().get(0);
        newSeries.getData().clear();
        XYChart.Data<String, Number> point;
        for (PriceBTC p : prices){
            point = new XYChart.Data<>(
                    p.getDatetime().toString(),
                    p.getPriceUSD()
            );
            newSeries.getData().add(point);
            Tooltip.install(point.getNode(), new MyTooltip(
                    "Date: "+ point.getXValue() + "\n" +
                            "Price: "+point.getYValue()
            ));
        }
    }

    private void updateData(){
        LinkedList<PriceBTC> prices = (LinkedList<PriceBTC>) ListPriceBTC.byScale(FetchPriceURL.fetchHistoricalPrice(this.startDate, this.endDate), this.scale);
//        LinkedList<PriceBTC> prices = (LinkedList<PriceBTC>) FetchPriceURL.fetchHistoricalPrice(this.startDate, this.endDate);
        this.feedData(prices);
    }

    private void updateThreshold(boolean isUpper){
        Series<String, Number> series = this.getData().get(isUpper ?1:2);
        series.getData().clear();
        int count = 0;
        for (Data<String, Number> d: dataSeries.getData()){
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

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        if (this.scale.equals(scale)) return;
        this.scale = scale;
        updateData();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if (this.startDate.equals(startDate)) return;
        this.startDate = startDate;
        updateData();
    }

    public double getLowerBound() {
        return this.lowerBound;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (this.endDate.equals(endDate)) return;
        this.endDate = endDate;
        updateData();
    }

    public void setDate(LocalDate startDate, LocalDate endDate){
        if (this.startDate.equals(startDate) && this.endDate.equals(endDate)) return;
        this.startDate = startDate;
        this.endDate = endDate;
        this.updateData();
    }

    public double getUpperBound() {
        return this.upperBound;
    }


    public void setLowerBound(double lowerBound) {
        if (this.lowerBound == lowerBound){
            return;
        }
        this.lowerBound = lowerBound;
        this.updateThreshold(false);
    }

    public void setUpperBound(double upperBound) {
        if (this.upperBound == upperBound) return;
        this.upperBound = upperBound;
        this.updateThreshold(true);
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
                sb.append(d.getXValue().toString());
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
