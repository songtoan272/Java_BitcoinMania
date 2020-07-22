package fxml.authentication;

import java.io.*;
import java.util.HashMap;

public class AccountsManager {
    public static HashMap<String, String> readAccounts() throws FileNotFoundException {
        HashMap<String, String> accounts = new HashMap<>();
        String delimiter = ",";
        try (BufferedReader br = new BufferedReader(new FileReader("./src/fxml/authentication/login/accounts.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                accounts.put(values[0], values[1]);
            }
        }
        catch (IOException e) {
            PrintWriter writer = new PrintWriter("./src/fxml/authentification/login/accounts.csv");
            writer.append("admin,admin\n");
            writer.flush();
            writer.close();
            accounts.put("admin", "admin");
        }
        return accounts;
    }

    public static void writeAccount(String username, String password){
        try (FileWriter fstream = new FileWriter("./src/fxml/authentication/login/accounts.csv", true)){
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(username+","+password);
            out.newLine();
            out.close();
        } catch (IOException e){
            System.out.println("Cannot register new account to accounts file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println(readAccounts().toString());

    }

}
