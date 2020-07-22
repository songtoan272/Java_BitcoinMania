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


    public static ArrayList<HashMap<String, Object>> select(String strSelect) throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, pw);   // For MySQL only
        // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(strSelect);
        stmt.close();
        conn.close();
        return MySQLServer.resultSetToArrayList(rset);
    }

    public static void insertOne(String strInsert) throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, pw); // for MySQL only
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(strInsert);
        stmt.close();
        conn.close();
    }

    public static void insertMany(String table, Map<String, List<String>> kvsInsert){
        try (
                Connection conn = DriverManager.getConnection(url, user, pw); // for MySQL only
                Statement stmt = conn.createStatement();
        ) {
//            list all columns
            ArrayList<String> cols = new ArrayList(kvsInsert.keySet());
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
        }
    }

}