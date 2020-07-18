package io.excel;

import javafx.util.Pair;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import price.PriceBTC;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public class ExcelReader {

    public static List<PriceBTC> read(File file){
        try
        {
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file

            //creating Workbook instance that refers to .xlsx file
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            //creating a Sheet object to retrieve object
            XSSFSheet sheet = wb.getSheetAt(0);

            LinkedList<PriceBTC> data = new LinkedList<>();
            for (int i=1; i<=sheet.getLastRowNum(); i++){
                data.add(new PriceBTC(
                        sheet.getRow(i).getCell(0).getDateCellValue(),
                        sheet.getRow(i).getCell(1).getNumericCellValue()
                ));
                System.out.println(
                        "row " + i + ":" +
                                "date: " +  data.get(i-1).getDatetime() + "; " +
                                "price: " + data.get(i-1).getPriceUSD());
            }
            return data;
        } catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static List<PriceBTC> read(String filePath)
    {
        try
        {
            File file = new File(filePath);   //creating a new file instance
            return ExcelReader.read(file);
        } catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Pair<LocalDateTime, Double>> readMap(File file){
        try
        {
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file

            //creating Workbook instance that refers to .xlsx file
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            //creating a Sheet object to retrieve object
            XSSFSheet sheet = wb.getSheetAt(0);

            List<Pair<LocalDateTime, Double>> data = new LinkedList<>();
            for (int i=1; i<=sheet.getLastRowNum(); i++){
                data.add(new Pair(LocalDateTime.ofInstant(
                        sheet.getRow(i).getCell(0).getDateCellValue().toInstant(),
                        ZoneId.systemDefault()),
                        sheet.getRow(i).getCell(1).getNumericCellValue()
                ));
                System.out.println(
                        "row " + i + ":" +
                                "date: " +  sheet.getRow(i).getCell(0).getDateCellValue().toString()+ "; " +
                                "price: " + sheet.getRow(i).getCell(1).getNumericCellValue());
            }
            return data;
        } catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String filePath = "/home/songtoan272/Documents/Cours/Semestre_5/Java_Bases/BitcoinMania/Project/Dataset/Fichier-Excel.xlsx";
        read(filePath);
    }
}
