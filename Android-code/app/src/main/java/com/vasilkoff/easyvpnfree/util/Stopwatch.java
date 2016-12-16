package com.vasilkoff.easyvpnfree.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Stopwatch {

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private Calendar calendar = Calendar.getInstance();
    private long startTime = System.currentTimeMillis();

    public Stopwatch() {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public long getDiff() {
        return System.currentTimeMillis() - startTime;
    }

    public String getElapsedTime() {
        calendar.setTimeInMillis(getDiff());
        return sdf.format(calendar.getTime());
    }
}
