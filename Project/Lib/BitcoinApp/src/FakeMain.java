import java.io.File;
import java.io.IOException;
import application.Main;

public class FakeMain {
    public static void main(String[] args) throws IOException {
        File f = new File(".");
        System.out.println("Execute from " + f.getCanonicalPath());
        Main.main(args);
    }
}
