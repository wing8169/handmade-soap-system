package handmadesoupmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @version 1.0
 * @author Chin Jia Xiong
 */
public class DatabaseOperation {
    
    /**
     * file name of database file
     */
    private static final String FILENAME = "jdbc:sqlite:" + System.getProperty("user.dir") + "/dat/HandmadeSoup.db";
    
    /**
     * connection instance
     */
    private static Connection conn;
    
    /**
     * create table
     * @param tableName name of the table
     * @param cols names for each column
     * @param defaults default values for each column
     */
    public static void createTable(String tableName, String[] cols, Object[] defaults, String[] types){
        try {
            connect();
            // oil table
            String command = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id integer PRIMARY KEY AUTOINCREMENT, ";
            for(int i=0; i<cols.length; i++){
                String type = types[i];
                switch (type) {
                    case "string":
                        command += cols[i] + " text default '" + defaults[i] + "'";
                        break;
                    case "integer":
                        command += cols[i] + " integer default " + defaults[i];
                        break;
                    case "double":
                        command += cols[i] + " double default " + defaults[i];
                        break;
                    default:
                        break;
                }
                if(i != cols.length-1) command += ", ";
            }
            command += ");";
            Statement statement = conn.createStatement();
            statement.execute(command);
            statement.close();
            close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * select all data and return the collected records
     * @param tableName name of the table
     * @return dataset
     */
    public static ArrayList<Object[]> getData(String tableName){
        ArrayList<Object[]> resultSet = new ArrayList<>();
        try {
            connect();
            String command = "SELECT * FROM " + tableName + ";";
            PreparedStatement statement = conn.prepareStatement(command);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ResultSetMetaData rsmd = rs.getMetaData();
                Object[] record = new Object[rsmd.getColumnCount()];
                for(int i=0; i<rsmd.getColumnCount(); i++){
                    record[i] = rs.getObject(i+1);
                }
                resultSet.add(record);
            }
            rs.close();
            statement.close();
            close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultSet;
    }
    
    /**
     * return the selected record
     * @param tableName name of the table
     * @param mode mode of the record
     * @return dataset
     */
    public static ArrayList<Object[]> getData(String tableName, String mode){
        ArrayList<Object[]> resultSet = new ArrayList<>();
        try {
            connect();
            String command = "SELECT * FROM " + tableName + " WHERE mode = '"+ mode +"';";
            PreparedStatement statement = conn.prepareStatement(command);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ResultSetMetaData rsmd = rs.getMetaData();
                Object[] record = new Object[rsmd.getColumnCount()];
                for(int i=0; i<rsmd.getColumnCount(); i++){
                    record[i] = rs.getObject(i+1);
                }
                resultSet.add(record);
            }
            rs.close();
            statement.close();
            close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultSet;
    }
    
    /**
     * return the selected record
     * @param tableName name of the table
     * @param name name of the historic data
     * @return dataset
     */
    public static Object[] getHistoricData(String tableName, String name){
        Object[] resultSet = null;
        try {
            connect();
            String command = "SELECT * FROM " + tableName + " WHERE name = '"+ name +"';";
            PreparedStatement statement = conn.prepareStatement(command);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            resultSet = new Object[rsmd.getColumnCount()];
            for(int i=0; i<rsmd.getColumnCount(); i++){
                resultSet[i] = rs.getObject(i+1);
            }
            rs.close();
            statement.close();
            close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultSet;
    }
    
    /**
     * return the selected record
     * @param tableName name of the table
     * @param id id of the record
     * @return dataset
     */
    public static Object[] getData(String tableName, int id){
        Object[] resultSet = null;
        try {
            connect();
            String command = "SELECT * FROM " + tableName + " WHERE id = "+ id +";";
            PreparedStatement statement = conn.prepareStatement(command);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            resultSet = new Object[rsmd.getColumnCount()];
            for(int i=0; i<rsmd.getColumnCount(); i++){
                resultSet[i] = rs.getObject(i+1);
            }
            rs.close();
            statement.close();
            close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultSet;
    }
    
    /**
     * remove data from given id
     * @param tableName table to be removed
     * @param id id to be removed
     */
    public static void removeData(String tableName, int id){
        try {
            connect();
            String command = "DELETE FROM " + tableName + " WHERE id = " + id + " ;";
            Statement statement = conn.createStatement();
            statement.execute(command);
            statement.close();
            close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * insert data into table
     * @param tableName table to insert
     * @param columns columns exist
     * @param values values of record to be inserted
     * @param types type of each column
     */
    public static void insertData(String tableName, String[] columns, Object[] values, String[] types){
        try {
            connect();
            String command = "INSERT INTO " + tableName + " (";
            for(int i=0; i<columns.length; i++){
                command += columns[i];
                if(i!=columns.length-1) command += ",";
            }
            command += ") VALUES (";
            for(int i=0; i<values.length; i++){
                command += "?";
                if(i!=values.length-1) command += ",";
            }
            command += ");";
            PreparedStatement statement = conn.prepareStatement(command);
            for(int i=1; i<=values.length; i++){
                String type = types[i-1];
                switch (type) {
                    case "string":
                        statement.setString(i, (String)values[i-1]);
                        break;
                    case "integer":
                        statement.setInt(i, (Integer)values[i-1]);
                        break;
                    case "double":
                        statement.setDouble(i, (Double)values[i-1]);
                        break;
                    default:
                        break;
                }
            }
            statement.executeUpdate();
            statement.close();
            close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * update existing data from table
     * @param tableName name of table
     * @param columns names of columns
     * @param values values for each column
     * @param id id to be updated
     * @param types type of each column
     */
    public static void updateData(String tableName, String[] columns, Object[] values, int id, String[] types){
        try {
            connect();
            String command = "UPDATE " + tableName + " SET ";
            for(int i=0; i<columns.length; i++){
                command += columns[i] + "=? ";
                if(i!=columns.length-1) command += ",";
            }
            command += "WHERE id=?;";
            PreparedStatement statement = conn.prepareStatement(command);
            for(int i=1; i<=values.length; i++){
                String type = types[i-1];
                switch (type) {
                    case "string":
                        statement.setString(i, (String)values[i-1]);
                        break;
                    case "integer":
                        statement.setInt(i, (Integer)values[i-1]);
                        break;
                    case "double":
                        statement.setDouble(i, (Double)values[i-1]);
                        break;
                    default:
                        break;
                }
            }
            statement.setInt(values.length+1, id);
            statement.executeUpdate();
            statement.close();
            close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void updateHistoricData(String tableName, String[] columns, Object[] values, String name, String[] types){
        try {
            connect();
            String command = "UPDATE " + tableName + " SET ";
            for(int i=0; i<columns.length; i++){
                command += columns[i] + "=? ";
                if(i!=columns.length-1) command += ",";
            }
            command += "WHERE name=?;";
            PreparedStatement statement = conn.prepareStatement(command);
            for(int i=1; i<=values.length; i++){
                String type = types[i-1];
                switch (type) {
                    case "string":
                        statement.setString(i, (String)values[i-1]);
                        break;
                    case "integer":
                        statement.setInt(i, (Integer)values[i-1]);
                        break;
                    case "double":
                        statement.setDouble(i, (Double)values[i-1]);
                        break;
                    default:
                        break;
                }
            }
            statement.setString(values.length+1, name);
            statement.executeUpdate();
            statement.close();
            close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * connect to database
     * @throws SQLException 
     */
    private static void connect() throws SQLException{
        conn = DriverManager.getConnection(FILENAME);
    }
    
    /**
     * close database connection
     * @throws SQLException 
     */
    private static void close() throws SQLException{
        conn.close();
    }
}
