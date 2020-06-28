package sql;

import java.sql.*;
import java.util.*;

public class MySQLServer {

    private static String url;
    private static String user;
    private static String pw;

    static {
        url = "jdbc:mysql://localhost:3306/bitcoin_mania?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
        user = "songtoan272";
        pw = "Casota272!";
    }

    public static void setUrl(String url) {
        MySQLServer.url = url;
    }

    public static void setUser(String user) {
        MySQLServer.user = user;
    }

    public static void setPw(String pw) {
        MySQLServer.pw = pw;
    }

    private static ArrayList resultSetToArrayList(ResultSet rs) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        ArrayList list = new ArrayList(50);
        while (rs.next()){
            HashMap row = new HashMap(columns);
            for(int i=1; i<=columns; ++i){
                row.put(md.getColumnName(i),rs.getObject(i));
            }
            list.add(row);
        }

        return list;
    }

    public static ArrayList select(String table, Map<String, String> kvs){
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(url, user, pw);   // For MySQL only
                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Step 3: Execute a SQL SELECT query. The query result is returned in a 'ResultSet' object.
            StringBuffer strSelect = new StringBuffer();
            strSelect.append("select * from " + table + " where ");
            for (Map.Entry<String, String> entry : kvs.entrySet()){
                strSelect.append(entry.getKey() + "='" + entry.getValue() + "' and ");
            }
            strSelect.replace(strSelect.length()-4, strSelect.length()-1, "\0");
            System.out.println("The SQL statement is: " + strSelect + "\n"); // Echo For debugging

            ResultSet rset = stmt.executeQuery(strSelect.toString());
            return MySQLServer.resultSetToArrayList(rset);
            // Step 4: Process the ResultSet by scrolling the cursor forward via next().
            //  For each row, retrieve the contents of the cells with getXxx(columnName).
//            System.out.println("The records selected are:");
//            int rowCount = 0;
//            while(rset.next()) {   // Move the cursor to the next row, return false if no more row
//                String username = rset.getString("username");
//                String password = rset.getString("password");
//                System.out.println(username + ", " + password);
//                ++rowCount;
//            }
//            System.out.println("Total number of records = " + rowCount);

        } catch(SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void insert(String table, Map<String, List<String>> kvsInsert){
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(url, user, pw); // for MySQL only

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Step 3 & 4: Execute a SQL INSERT|DELETE statement via executeUpdate(),
            //   which returns an int indicating the number of rows affected.

            //list all columns
            ArrayList cols = new ArrayList(kvsInsert.keySet());
            StringBuffer colsStr = new StringBuffer("(");
            for (int i = 0; i < cols.size(); i++){
                colsStr.append(cols.get(i) + ", ");
            }
            colsStr.replace(colsStr.length()-2, colsStr.length(), ")");
            System.out.println("cols = " + colsStr);

            // INSERT many record
            for (int i=0; i < 2; i++){
                //values to insert
                StringBuffer values = new StringBuffer("(");
                for (int j = 0; j < cols.size(); j++){
                    values.append("'" + kvsInsert.get(cols.get(j)).get(i) + "', ");
                }
                values.delete(values.length()-2, values.length()).append(")");

                String sqlInsert = String.format("insert into %s %s values %s",
                        table, colsStr, values);
                System.out.println("The SQL statement is: " + sqlInsert + "\n");  // Echo for debugging
                int countInserted = stmt.executeUpdate(sqlInsert);
                System.out.println(countInserted + " records inserted.\n");
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }  // Step 5: Close conn and stmt - Done automatically by try-with-resources
    }

    public static void delete(String table, Map<String, List<String>> kvsInsert) {
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        "myuser", "xxxx"); // for MySQL only

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Step 3 & 4: Execute a SQL INSERT|DELETE statement via executeUpdate(),
            //   which returns an int indicating the number of rows affected.

            // DELETE records with id>=3000 and id<4000
            String sqlDelete = "delete from books where id >= 3000 and id < 4000";
            System.out.println("The SQL statement is: " + sqlDelete + "\n");  // Echo for debugging
            int countDeleted = stmt.executeUpdate(sqlDelete);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }  // Step 5: Close conn and stmt - Done automatically by try-with-resources
    }

    public static void main(String[] args) {
        Map<String, List<String>> kvsInsert = new HashMap<>();
        kvsInsert.put("username", Arrays.asList("test1", "test2"));
        kvsInsert.put("password", Arrays.asList("pass1", "pass2"));
        MySQLServer.insert("authentification", kvsInsert);
        MySQLServer.select("authentification", new HashMap<String, String>(){
            {
                put("username", "test1");
                put("password", "pass1");
            }
        });
    }
}