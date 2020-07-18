
package io;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import price.PriceBTC;
import price.ListPriceBTC;

public interface Export {
    public void exportCSVFile(File file);
    public void exportSQLFile(File file);
}