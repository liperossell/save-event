package com.ciliosencantados.util;

import com.google.api.client.util.DateTime;

public class DateTimeUtil {
    private static final String AT_START_OF_DAY_STRING = "T00:00:00-03:00";
    private static final String AT_END_OF_DAY_STRING = "T23:59:59-03:00";

    public static DateTime atStartOfDay(DateTime value) {
        String string = getDateString(value);
        return new DateTime(string + AT_START_OF_DAY_STRING);
    }

    public static DateTime atEndOfDay(DateTime value) {
        String string = getDateString(value);
        return new DateTime(string + AT_END_OF_DAY_STRING);
    }

    private static String getDateString(DateTime value) {
        return value.toString().substring(0, 10);
    }

    public static DateTime addHours(DateTime dateTime, int hours) {
        long newValue = fromHoursToMilli(hours);
        return addTime(dateTime, newValue);
    }

    private static long fromHoursToMilli(long hours) {
        return hours * 60 * 60 * 1000;
    }

    public static DateTime addTime(DateTime dateTime, long value) {
        long newValue = dateTime.getValue() + value;

        return new DateTime(newValue);
    }
}
