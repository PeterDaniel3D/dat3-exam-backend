package entities;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

class AuctionTest {

    @Test
    void rndFormattedDateAndTimeFromLong() {
        System.out.println("Test: Formatted date and time");
        System.out.println("----------------------------------------------");

        Date date = new Date();
        System.out.println("Date            : " + date);

        Timestamp timestamp = new Timestamp(date.getTime());
        System.out.println("Timestamp       : " + timestamp);

        System.out.println("");
        System.out.println("Date (long)     : " + date.getTime());

        long time = new Timestamp(date.getTime()).getTime();
        System.out.println("Timestamp (long): " + time);

        System.out.println("");

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm");
        sdfDate.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        sdfTime.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        String formattedDate = sdfDate.format(time);
        String formattedTime = sdfTime.format(time);
        System.out.println("Date (String)   : " + formattedDate);
        System.out.println("Time (String)   : " + formattedTime);
    }
}