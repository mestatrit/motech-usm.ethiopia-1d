package org.gates.ethiopia.scheduling;

import org.gates.ethiopia.constants.MotechConstants;
import org.joda.time.DateTime;
import org.motechproject.model.DayOfWeek;

public final class GenerateDateTimeUtil {

    private GenerateDateTimeUtil() {
    }

    public static DateTime nextTime(String dayDue, int hourDue, int minuteDue) {

        int dayValue = getDayValue(dayDue.trim().toLowerCase());

        DateTime now = DateTime.now();

        DateTime enrollTime;

        if (now.dayOfWeek().get() == dayValue) {
            enrollTime = now.withHourOfDay(hourDue).withMinuteOfHour(minuteDue).minusWeeks(1);
        } else if (now.dayOfWeek().get() > dayValue) {
            enrollTime = now.withHourOfDay(hourDue).withMinuteOfHour(minuteDue).withDayOfWeek(dayValue);
        } else {
            enrollTime = now.withHourOfDay(hourDue).withMinuteOfHour(minuteDue).withDayOfWeek(dayValue).minusWeeks(1);
        }

        return enrollTime;
    }

    public static int getDayValue(String dayDue) {

        switch (dayDue) {
            case "monday": return DayOfWeek.Monday.getValue();
            case "tuesday": return DayOfWeek.Tuesday.getValue();
            case "wednesday": return DayOfWeek.Wednesday.getValue();
            case "thursday": return DayOfWeek.Thursday.getValue();
            case "friday": return DayOfWeek.Friday.getValue();
            case "saturday": return DayOfWeek.Saturday.getValue();
            case "sunday": return DayOfWeek.Sunday.getValue();
            default : return DayOfWeek.Sunday.getValue();
        }
    }

    public static DateTime nextTime(int day) {

        DateTime now = DateTime.now();

        DateTime enrollTime;

        if (now.dayOfWeek().get() == day) {
            enrollTime = now.withHourOfDay(MotechConstants.HOUR_DUE).withMinuteOfHour(MotechConstants.MINUTE_DUE).minusWeeks(1);
        } else if (now.dayOfWeek().get() > day) {
            enrollTime = now.withHourOfDay(MotechConstants.HOUR_DUE).withMinuteOfHour(MotechConstants.MINUTE_DUE).withDayOfWeek(MotechConstants.DAY_DUE);
        } else {
            enrollTime = now.withHourOfDay(MotechConstants.HOUR_DUE).withMinuteOfHour(MotechConstants.MINUTE_DUE).withDayOfWeek(MotechConstants.DAY_DUE).minusWeeks(1);
        }

        return enrollTime;
    }
}
