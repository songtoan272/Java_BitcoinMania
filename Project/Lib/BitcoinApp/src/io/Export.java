package io;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Export {
    public static void export(String fileType, List<Map<String, String>> data) {
        switch(fileType) {
            case "CSV":
                this.exportCSVFile(data);
            case:

        }
    }
    private static void exportCSVFile(List<Map<String, String>> data) {
        try (PrintWriter writer = new PrintWriter(new File("export.csv"))) {

            StringBuilder sb = new StringBuilder();
            sb.append("id,");
            sb.append(',');
            sb.append("date");
            sb.append(',');
            sb.append("value");
            sb.append('\n');

            for (int i = 0; i < data.size(); i++ ) {
                sb.append(i + ",");
                sb.append(data[i].getKey());
                sb.append(",");
                sb.append(data[i].getValue());
                sb.append("\n");
            }

            writer.write(sb.toString());

            System.out.println("done exporting CSV file!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void exportSQLFile(List<Map<String, String>> data) {
        StringBuilder table = "table"
        try (PrintWriter writer = new PrintWriter(new File("export.sql"))) {
            StringBuilder sb = new StringBuilder();

            //All insertion line
            for (int i = 0; i < data.size(); i++ ) {
                sb.append("INSERT INTO" + table + "(date, value) VALUES ('" + data[i].getKey() + "', '" + data[i].getValue() + "')");
                sb.append("\n");
            }

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}