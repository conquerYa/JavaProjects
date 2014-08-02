/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cash2;

import java.sql.Date;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author Andrey
 */
public class ParseDate {

    static boolean isValid(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy");
        boolean t = true;
        try {
            java.util.Date test = sdf.parse(a);
        } catch (ParseException pe) {
            t = false;
        }
        return t;
    }

    static String dateToString(String a, String format) throws ParseException {
        final String OLD_FORMAT = "yyyy-MM-dd";
        final String NEW_FORMAT = format;
        String newD;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        java.util.Date d = sdf.parse(a.trim());
        sdf.applyPattern(NEW_FORMAT);
        newD = sdf.format(d);
        return newD;
    }

    static Date stringToDate(String a) throws ParseException {
        if(a.equals(""))
            return null;
        final String OLD_FORMAT = "dd.MM.yyyy";
        final String NEW_FORMAT = "yyyy-MM-dd";
        String newD;

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        java.util.Date d = sdf.parse(a);
        sdf.applyPattern(NEW_FORMAT);
        newD = sdf.format(d);
        java.sql.Date dt = java.sql.Date.valueOf(newD);
        return dt;
    }

    static String mToString(int month) {
        return new DateFormatSymbols(new Locale("ru")).getMonths()[month - 1];
    }

    static int mToInt(String month) {
        try {
            java.util.Date date = new SimpleDateFormat("MMM", new Locale("ru")).parse(month);
            Calendar cal = Calendar.getInstance(new Locale("ru"));
            cal.setTime(date);
            return cal.get(Calendar.MONTH) + 1;
        } catch (ParseException ex) {
            System.out.println("Can't parse string month to integer");
        }
        return 0;
    }

    static boolean isValid(String begin, String end) {
        try {
            if(stringToDate(begin).before(stringToDate(end)))
                return true;
        } catch (ParseException ex) {
            System.out.println("Can't parse Date" + ex);        }
        return false;
    }

}
