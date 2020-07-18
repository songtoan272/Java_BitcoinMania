package io.sql;

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
        Class<MySQLServer> clazz = MySQLServer.class;
//        Constructor<MySQLServer> cons = clazz.getDeclaredConstructor();
//        cons.setAccessible(false);

    }

    public static void setUser(String user) {
        MySQLServer.user = user;
    }

    public static void setPw(String pw) {
        MySQLServer.pw = pw;
    }

    private static ArrayList<HashMap<String, Object>> resultSetToArrayList(ResultSet rs) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        ArrayList<HashMap<String, Object>> list = new ArrayList<>(50);
        while (rs.next()){
            HashMap<String, Object> row = new HashMap<>(columns);
            for(int i=1; i<=columns; ++i){
                row.put(md.getColumnName(i),rs.getObject(i));
            }
            list.add(row);
        }

        return list;
    }


    public static ArrayList<HashMap<String, Object>> select(String strSelect){
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(url, user, pw);   // For MySQL only
                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
//            System.out.println("The SQL statement is: " + strSelect + "\n"); // Echo For debugging

            ResultSet rset = stmt.executeQuery(strSelect);
            return MySQLServer.resultSetToArrayList(rset);

        } catch(SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void insertOne(String strInsert){
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(url, user, pw); // for MySQL only

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Step 3 & 4: Execute a SQL INSERT|DELETE statement via executeUpdate()
            stmt.executeUpdate(strInsert);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }  // Step 5: Close conn and stmt - Done automatically by try-with-resources
    }

    public static void insertMany(String table, Map<String, List<String>> kvsInsert){
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(url, user, pw); // for MySQL only

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            // Step 3 & 4: Execute a SQL INSERT|DELETE statement via executeUpdate(),
            //   which returns an int indicating the number of rows affected.

//            list all columns
                ArrayList cols = new ArrayList(kvsInsert.keySet());
                StringBuffer colsStr = new StringBuffer("(");
                for (int i = 0; i < cols.size(); i++){
                    colsStr.append(cols.get(i) + ", ");
                }
                colsStr.replace(colsStr.length()-2, colsStr.length(), ")");
                System.out.println("cols = " + colsStr);

            // INSERT many record
            for (int i=0; i < kvsInsert.size(); i++){
//                values to insert
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
        kvsInsert.put("username", Arrays.asList("damien", "thomas"));
        kvsInsert.put("password", Arrays.asList("damien", "thomas"));
        MySQLServer.insertMany("authentification", kvsInsert);
        System.out.println(MySQLServer.select("select * from authentification where username='damien' and password='damien'").toString());
    }
}