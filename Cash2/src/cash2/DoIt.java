/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cash2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Andrey
 */
public class DoIt {
    public static void main(String args[]) throws SQLException{
         ConnectDB x= new ConnectDB();
         int nal = 0;
         ResultSet rs = x.retQuery("select suma,day from outlay where supply=36");
         while(rs.next()){             
            int a = x.query("update day set rest="+rs.getInt(1)+" where id ="+ rs.getInt(2));
         }
         rs = x.retQuery("select day from outlay  where supply=34");
         while(rs.next()){   
            ResultSet rs2 = x.retQuery("select suma from outlay  where day="+ rs.getInt(1)+" and supply<>21 and supply<>22");
            while(rs2.next()){
                nal+=rs2.getInt(1);
            }
            int a = x.query("update day set nal="+nal+" where id ="+ rs.getInt(1));
            nal = 0;
         }
    }
    
}
