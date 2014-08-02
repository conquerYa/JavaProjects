/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cash2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Andrey
 */
public class ConnectDB {
    private Connection connection = null;

    public ConnectDB() {

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            //String strUrl = "jdbc:derby:/home/andrey/Cash;user=andrey;password=andrew123";
            String strUrl = "jdbc:derby:C:\\dist\\Cash;user=andrey;password=andrew123";
            //String strUrl = "jdbc:derby:C:\\Users\\Andrew\\Documents\\NetBeansProjects\\Cash\\Cash;user=andrey;password=andrew123";
            connection = DriverManager.getConnection(strUrl);

        } catch (Exception err) {
            System.out.println("Can't connect to database");
        }
    }
    int query(String sql){
        int rs = 0;
        Statement s = null;
        try {
            s = connection.createStatement();
            rs = s.executeUpdate(sql);

        } catch (Exception e) {
            System.out.println("Can't execute query "+sql+e);
        }
        return rs;
    }
    ResultSet retQuery(String sql){
        ResultSet rs = null;
        Statement s = null;
        try {
            s = connection.createStatement();
            rs = s.executeQuery(sql);

        } catch (SQLException ex) {
            System.out.println("Can't execute query: "+sql+ex);
        }
        return rs;     
    }
    
    
}
