/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.essc.web.util;

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
    public void testFormat_Date_Date() throws ParseException {
        System.out.println("format");
        Date when = new SimpleDateFormat("yyyy-MM-dd").parse("2013-01-01");
        Date now  = new SimpleDateFormat("yyyy-MM-dd").parse("2013-01-11");
        String expResult = "10 days ago";
        String result = SimpleRelativeDateFormatter.format(when, now);
        assertEquals(expResult, result);
    }
    
}