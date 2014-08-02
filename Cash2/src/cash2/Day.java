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
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrey
 */
public class Day {

    ConnectDB x;

    public Day() {
        x = new ConnectDB();
    }

    public int addDay(String date, Integer rest, Integer nal, Map merchants) {
        int a = 0;
        int cash = 0;
        String name;
        int hours, id_day = 0, id_merchant = 0;
        try {
            ResultSet rs;
            id_day = getDayId(date);
            if (id_day == 0) {
                Date before = new Date(ParseDate.stringToDate(date).getTime() - 86400000);
                rs = x.retQuery("select rest from day where date='" + before + "'");
                if (rs.next()) {
                    cash = rest + nal - rs.getInt(1);
                    a = x.query("insert into day(date,cash,rest,nal)values('" + ParseDate.stringToDate(date) + "'," + cash + "," + rest + "," + nal + ")");
                    id_day = getDayId(date);
                    Iterator it = merchants.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry) it.next();
                        name = (String) pairs.getKey();
                        hours = (int) pairs.getValue();
                        rs = x.retQuery("select id from merchant where fname='" + name + "'");
                        if (rs.next()) {
                            id_merchant = rs.getInt(1);
                        }
                        a = x.query("insert into merchant_day values(" + id_merchant + "," + id_day + "," + hours + ")");
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                } else {
                    a = -2;
                }
            }
        } catch (ParseException ex) {
            System.out.println("Can't parse date in day addDay " + ex);
        } catch (SQLException ex) {
            System.out.println("error sql in Day::addDay " + ex);
        }
        return a;
    }

    public int getDayId(String date) {
        try {
            ResultSet rs = x.retQuery("select id from day where date='" + ParseDate.stringToDate(date) + "'");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (ParseException ex) {
            System.out.println("Can't parse date in day getDayId " + ex);
        } catch (SQLException ex) {
            System.out.println("error sql in Day::getDayId " + ex);
        }
        return 0;
    }

    public int deleteDay(String date) {
        int a = 0;
        try {
            int id = getDayId(date);
            a = x.query("delete from merchant_day where id_day=" + id);
            a = x.query("delete from day where date='" + ParseDate.stringToDate(date) + "'");
        } catch (ParseException ex) {
            System.out.println("Can't parse date in day deleteDay " + ex);
        }
        return a;
    }

    public int getCash(String begin, String end) {
        try {
            int cash = 0;
            ResultSet rs = x.retQuery("select cash from day where date between '" + ParseDate.stringToDate(begin) + "' and '" + ParseDate.stringToDate(end) + "'");
            while (rs.next()) {
                cash += rs.getInt(1);
            }
            return cash;
        } catch (ParseException ex) {
            System.out.println("Can't parse date in day getCash");
        } catch (SQLException ex) {
            System.out.println("Can't getCash of period");
        }
        return 0;
    }

    public ResultSet getDayCash() {
        return x.retQuery("select date,cash from day order by date desc");
    }

    public ResultSet getCash(String date) {
        try {
            return x.retQuery("select cash from day where date='" + ParseDate.stringToDate(date)+"'");
        } catch (ParseException ex) {
            System.out.println("Parsing error in getCash");
        }
        return null;
    }

    ArrayList<String> getPartOfDate(String part) {
        ArrayList<String> list = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = x.retQuery("SELECT DISTINCT " + part + "(date) FROM day order by " + part + "(date) asc");
            while (rs.next()) {
                String res;
                if (part.equals("MONTH")) {
                    res = ParseDate.mToString(rs.getInt(1));
                } else {
                    res = rs.getString(1);
                }
                list.add(res);
            }
            return list;
        } catch (SQLException ex) {
            System.out.println("Can't get " + part + " for jcombobox in mframe");
        }
        return list;

    }

    ResultSet getMonthStat(int month, int year) {
        return x.retQuery("select cash,date from day where month(date)=" + month + " and year(date)=" + year + " order by date");
    }

    int getRest(String day) {
        int res = 0;
        try {
            ResultSet rs = x.retQuery("select rest from day where date='" + ParseDate.stringToDate(day) + "'");
            if (rs.next()) {
                res = rs.getInt(1);
            }
        } catch (ParseException ex) {
            System.out.println("Error in parsing in Day::getRestNal");
        } catch (SQLException ex) {
            System.out.println("Error in executing sql in Day::getRestNal");
        }
        return res;
    }

    int getNal(String day) {
        int nal = 0;
        try {
            ResultSet rs = x.retQuery("select nal from day where date='" + ParseDate.stringToDate(day) + "'");
            if (rs.next()) {
                nal = rs.getInt(1);
            }
        } catch (ParseException ex) {
            System.out.println("Error in parsing in Day::getRestNal");
        } catch (SQLException ex) {
            System.out.println("Error in executing sql in Day::getRestNal");
        }
        return nal;
    }

    int updateDay(String date, int rest, int nal, Map<String, Integer> merchants) {
        int a = 0;
        int cash = 0;
        ResultSet rs = getCash(date);
        try {
            if (rs.next()) {
                cash = rs.getInt(1);
                cash += (rest - getRest(date)) + (nal - getNal(date));
                int id_day = getDayId(date);
                a = x.query("update day set rest=" + rest + ",nal=" + nal+",cash="+cash+"where id="+id_day);
                int id_merchant = 0;
                a = x.query("delete from merchant_day where id_day=" + id_day);
                Iterator it = merchants.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    String name = (String) pairs.getKey();
                    int hours = (int) pairs.getValue();
                    rs = x.retQuery("select id from merchant where fname='" + name + "'");
                    try {
                        if (rs.next()) {
                            id_merchant = rs.getInt(1);
                        }
                    } catch (SQLException ex) {
                        System.out.println("Error in Updating Day");
                    }
                    a = x.query("insert into merchant_day values(" + id_merchant + "," + id_day + "," + hours + ")");
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        } catch (SQLException ex) {
            System.out.println("Sql error in Updating Day");
        }
        return a;
    }
}
