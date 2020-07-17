package excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

public class ExcelReader {

    public static HashMap<String, Object> read(String filePath)
    {
        try
        {
            File file = new File(filePath);   //creating a new file instance
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file

            //creating Workbook instance that refers to .xlsx file
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            //creating a Sheet object to retrieve object
            XSSFSheet sheet = wb.getSheetAt(0);

            HashMap<String, Object> data = new HashMap<String, Object>();
            LinkedList<Date> datetime = new LinkedList<>();
            LinkedList<Double> price = new LinkedList<>();
            data.put("Datetime", datetime);
            data.put("Price", price);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            for (int i=1; i<=sheet.getLastRowNum(); i++){
                System.out.println(sheet.getRow(i).getCell(0).getDateCellValue());
                System.out.println(sheet.getRow(i).getCell(1).getNumericCellValue());
                datetime.add(sheet.getRow(i).getCell(0).getDateCellValue());
                price.add(sheet.getRow(i).getCell(1).getNumericCellValue());
                System.out.println(
                        "row: " + i + "\n" +
                        "date: " + ((LinkedList<Date>) data.get("Datetime")).get(i-1) + "\n" +
                        "price: " + ((LinkedList<Double>) data.get("Price")).get(i-1));
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
