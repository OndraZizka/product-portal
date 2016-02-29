package org.jboss.essc.web.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ondra
 */
public class SimpleRelativeDateFormatterTest {
    
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
    
    
    public SimpleRelativeDateFormatterTest() {
    }

    @Test
    public void testFormat_Date() {
        System.out.println("format");
        Date date = new Date();
        String expResult = "now";
        String result = SimpleRelativeDateFormatter.format(date);
        assertEquals(expResult, result);
    }

    @Test
    public void testFormat_10days() throws ParseException {
        testComparison("2013-01-01", "2013-01-11", "10 days ago");
    }
    
    @Test
    public void testFormat_3weeks() throws ParseException {
        testComparison("2013-01-24", "2013-02-11", "2 weeks ago");
    }
    
    @Test
    public void testFormat_3weeks_roundUp() throws ParseException {
        testComparison("2013-01-01", "2013-01-20", "3 weeks ago");
    }
    
    @Test
    public void testFormat_1year() throws ParseException {
        testComparison("2013-01-24", "2014-01-24", "a year ago");
    }

    private static void testComparison(String when, String now, String expected) throws ParseException {
        System.out.println( String.format("%s -> %s => %s", when, now, expected) );
        Date when_ = DF.parse(when);
        Date now_  = DF.parse(now);
        String result = SimpleRelativeDateFormatter.format(when_, now_);
        assertEquals(expected, result);
    }
    
}// class