package org.gates.ethiopia.constants;

import org.motechproject.commons.date.model.DayOfWeek;

public final class MotechConstants {

    // Scheduling constants
    public static final String SCHEDULE_NAME_KEY = "schedule_name";

    public static final String LATE_WINDOW = "late";

    public static final String SCHEDULE_FIELD_NAME = "healthWorkerSchedule";

    public static final String SCHEDULE_DAY_OF_WEEK_FIELD = "scheduleDayOfWeek";

    public static final String SCHEDULE_HOUR_OF_DAY_FIELD = "scheduleHourOfDay";

    public static final String SCHEDULE_MINUTE_OF_HOUR_FIELD = "scheduleMinuteOfHour";

    public static final String SCHEDULE_EMAIL_DELAY_IN_MINUTES_FIELD = "emailDelayInMinutes";

    public static final String SCHEDULE_EMAIL_REPEAT_IN_HOURS_FIELD = "emailFrequencyInHours";
    
    public static final String PREVIOUS_DAYS_TO_CHECK_FIELD = "previousDaysToCheck";

    public static final String MAPPING_FILE_NAME = "hew-schedule.json";

    public static final String DEMO_MAPPING_FILE_NAME = "hew-demo-schedule.json";

    // E-mail constants
    public static final String WOREDA_PLACEHOLDER = "[WOREDA]";

    public static final String FACILITY_PLACEHOLDER = "[HEALTHPOST]";

    public static final String REMINDER_BODY = "body";

    public static final String REMINDER_SUBJECT = "subject";

    // Time due constants
    public static final int DAY_DUE = DayOfWeek.Sunday.getValue();

    public static final int HOUR_DUE = 23;

    public static final int MINUTE_DUE = 59;

    public static final int SECONDS_IN_MINUTE = 60;

    public static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;

    public static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;

    public static final int SECONDS_IN_WEEK = SECONDS_IN_DAY * 7;

    // Querying events

    public static final String EXTERNAL_ID = "externalId";

    // Redelivery

    public static final int RANDOM_MINUTES = 239;

    public static final String RECIPIENT = "recipient";

    public static final String EMAIL_ADDRESS = "email";

    public static final String REGION = "region";

    public static final String WOREDA = "woreda";

    public static final String FACILITY_NAME = "facility_name";

    public static final int NUM_DAYS_TO_CHECK = 7;

    public static final int YEAR = 2012;

    public static final String EMAIL_SUBJECT = "subject";

    public static final CharSequence REGION_PLACEHOLDER = "[REGION]";

    public static final String DEFAULT_EMAIL = "default_email";

    public static final String LAST_SUBMITTED = "last_submitted";

    public static final String EMAIL_SETTINGS_FILE_NAME = "emails1B.json";

    private MotechConstants() {
    }

}
