/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cash2;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrey
 */
public class Merchant {

    ConnectDB x;

    public Merchant() {
        x = new ConnectDB();
    }

    int addMerchant(String name, String start_w) {
        try {
            if (getMerchId(name) == 0) {
                return x.query("insert into merchant(fname,start_w,working) values('" + name + "','" + ParseDate.stringToDate(start_w) + "',1)");
            }
        } catch (ParseException ex) {
            System.out.println("Can't parse date in Merchant::addMerchant");
        } catch (SQLException ex) {
            System.out.println("Can't execute in Merchant::addMerchant");
        }
        return 0;
    }

    int editMerchant(String newName, String begin, String end, int working, String oldName) {
        try {
            Date a = ParseDate.stringToDate(end);
            String d;
            if(a==null){
                d = null;
            }else{
                d = "'"+a+"'";
            }
            return x.query("update merchant set fname='" + newName + "',start_w='" + ParseDate.stringToDate(begin) + "',end_w=" + d + ",working="+working+" where fname='"+oldName+"'");
        } catch (ParseException ex) {
            System.out.println("Can't parse date in Merchant::editMerchant");

        }
        return 0;

    }

    ArrayList<String> getListOfWorkingMerchants() {
        ArrayList<String> list = new ArrayList<String>();
        ResultSet rs = x.retQuery("SELECT fname FROM merchant where working=1");
        try {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("Can't add name to list in Merchant::getListOfMerchants");
        }
        return list;
    }
     ArrayList<String> getListOfAllMerchants() {
        ArrayList<String> list = new ArrayList<String>();
        ResultSet rs = x.retQuery("SELECT fname FROM merchant");
        try {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("Can't add name to list in Merchant::getListOfMerchants");
        }
        return list;
    }

    Map<String,Integer> getMerchantsHours(String date) {
        Map<String,Integer> merchs = new HashMap<>();
        try {
            ResultSet rs = x.retQuery("select fname,hours from merchant m join merchant_day on m.id=id_merch\n"
                    + "join day d on  d.id=id_day  where d.date='" + ParseDate.stringToDate(date) + "'");
            while (rs.next()) {
                merchs.put(rs.getString(1), rs.getInt(2));
            }
        } catch (ParseException ex) {
            System.out.println("Can't parse date in Merchant::getMerchantsHours");
        } catch (SQLException ex) {
            System.out.println("Can't execute in Merchant::getMerchantsHours");
        }
        return merchs;
    }

    String getStartDate(String name) {
        try {
            ResultSet rs = x.retQuery("select start_w from merchant where fname='" + name + "'");
            if (rs.next()) {
                if (rs.getString(1) != null) {
                    return ParseDate.dateToString(rs.getString(1), "dd.MM.yyyy");
                }
            }
        } catch (SQLException ex) {
            System.out.println("can't execute sql in Merchant::getStartDate" + ex);
        } catch (ParseException ex) {
            Logger.getLogger(Merchant.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    String getEndDate(String name) {
        try {
            ResultSet rs = x.retQuery("select end_w from merchant where fname='" + name + "'");
            if (rs.next()) {
                if (rs.getString(1) != null) {
                    return ParseDate.dateToString(rs.getString(1), "dd.MM.yyyy");
                }
            }
        } catch (SQLException ex) {
            System.out.println("can't execute sql in Merchant::getEndDate" + ex);
        } catch (ParseException ex) {
            Logger.getLogger(Merchant.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getMerchId(String name) throws ParseException, SQLException {
        ResultSet rs = x.retQuery("select id from merchant where fname='" + name + "'");
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    int getMerchHours(String name, String begin, String end) {

        int a = 0;
        try {
            ResultSet rs = x.retQuery("select hours from merchant_day where id_day IN(select id from day where date between '" + ParseDate.stringToDate(begin) + "' and '" + ParseDate.stringToDate(end) + "')and id_merch IN(select id from merchant where fname='" + name + "')");
            while (rs.next()) {
                a += rs.getInt(1);
            }
        } catch (ParseException ex) {
            System.out.println("Can't parse date in Merchant::getMerchHours");
        } catch (SQLException ex) {
            System.out.println("Can't execute in Merchant::getMerchHours");
        }
        return a;
    }

}
