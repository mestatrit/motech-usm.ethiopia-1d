package org.gates.ethiopia.scheduling;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.gates.ethiopia.constants.EventConstants;
import org.gates.ethiopia.constants.MotechConstants;
import org.gates.ethiopia.service.GatesEthiopiaMailService;
import org.joda.time.DateTime;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.motechproject.eventlogging.service.EventQueryService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.gson.reflect.TypeToken;

@Component
public class EmailScheduler {

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    private static final long MILLIS_IN_HOUR = 3600000;

    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    @Autowired
    private MotechSchedulerService schedulerService;

    @Autowired
    private EventQueryService<CouchEventLog> eventQueryService;

    @Autowired
    private GatesEthiopiaMailService mailService;

    @Autowired
    private SettingsFacade settingsFacade;

    @PostConstruct
    public void initiateJob() {

        MotechEvent combineEmails = new MotechEvent(EventConstants.COMBINE_EMAILS);

        RepeatingSchedulableJob job = new RepeatingSchedulableJob();

        String day = settingsFacade.getProperty(MotechConstants.SCHEDULE_DAY_OF_WEEK_FIELD);

        String hour = settingsFacade.getProperty(MotechConstants.SCHEDULE_HOUR_OF_DAY_FIELD);

        String minute = settingsFacade.getProperty(MotechConstants.SCHEDULE_MINUTE_OF_HOUR_FIELD);

        String repeatInHours = settingsFacade.getProperty(MotechConstants.SCHEDULE_EMAIL_REPEAT_IN_HOURS_FIELD);

        String delayInMinutes = settingsFacade.getProperty(MotechConstants.SCHEDULE_EMAIL_DELAY_IN_MINUTES_FIELD);

        long repeatInMillis = Integer.parseInt(repeatInHours) * MILLIS_IN_HOUR;

        int startDay = GenerateDateTimeUtil.getDayValue(day);

        int hourOfDay = Integer.parseInt(hour);

        int minuteOfHour = Integer.parseInt(minute);

        int delayInMinutesValue = Integer.parseInt(delayInMinutes);

        DateTime startTime = new DateTime().withDayOfWeek(startDay).withHourOfDay(hourOfDay).withMinuteOfHour(minuteOfHour).plusMinutes(delayInMinutesValue);

        logger.info("Starting aggregate e-mail job at: " + startTime.toDate());

        job.setStartTime(startTime.toDate());
        job.setRepeatIntervalInMilliSeconds(repeatInMillis);
        job.setMotechEvent(combineEmails);

        schedulerService.safeScheduleRepeatingJob(job);
    }

    //@MotechListener(subjects = EventConstants.AGGREGATED_EVENT)
    @MotechListener(subjects = EventConstants.COMBINE_EMAILS)
    public void handleJob(MotechEvent event) {

        logger.warn("Handled event: " + event.toString());
        Map<String, List<CouchEventLog>> emailBuilderMap = new HashMap<String, List<CouchEventLog>>();

        List<CouchEventLog> logs = eventQueryService.getAllEventsBySubject(EventConstants.LATE_EVENT);

        for (CouchEventLog log : logs) {

            DateTime logTime = log.getTimeStamp();

            String region = (String) log.getParameters().get(MotechConstants.REGION);

            if (region == null || region.trim().length() == 0) {
                region = "No region listed";
            }

            region = region.trim().toLowerCase();

            String previousDays = settingsFacade.getProperty(MotechConstants.PREVIOUS_DAYS_TO_CHECK_FIELD);

            int previousDaysValue = Integer.parseInt(previousDays);

            if (logTime.isAfter(DateTime.now().minusDays(previousDaysValue))) {
                List<CouchEventLog> couchLogs = emailBuilderMap.get(region);
                if (couchLogs == null) {
                    couchLogs = new ArrayList<CouchEventLog>();
                    couchLogs.add(log);
                    emailBuilderMap.put(region, couchLogs);
                } else {
                    couchLogs.add(log);
                    emailBuilderMap.put(region, couchLogs);
                }

            }
        }
        logger.warn("about to construct e-mails");
        constructEmails(emailBuilderMap);
    }

    private void constructEmails(Map<String, List<CouchEventLog>> emailBuilderMap) {
        for (String region : emailBuilderMap.keySet()) {
            List<CouchEventLog> logs = emailBuilderMap.get(region);
            buildEmail(region, logs);
        }
    }

    private void buildEmail(String region, List<CouchEventLog> logs) {
        List<String> recipients = getEmailForRegion(region);
        StringBuilder stringBuilder = new StringBuilder("The following facilities have not submitted a report within the last seven days: \n\n");

        for (int i = 0; i < logs.size(); i++) {
            logger.warn("Building e-mail...");
            stringBuilder.append((i + 1) + "." + "  Woreda: " + logs.get(i).getParameters().get(MotechConstants.WOREDA) + "   |  Facility: " + logs.get(i).getParameters().get(MotechConstants.FACILITY_NAME) + " | Last submission date: " + logs.get(i).getParameters().get(MotechConstants.LAST_SUBMITTED) + "\n");
        }      

        mailService.sendAggregateEmailReminder(recipients, stringBuilder.toString(), settingsFacade.getProperty(MotechConstants.EMAIL_SUBJECT).replace(MotechConstants.REGION_PLACEHOLDER, region));
    }

    private List<String> getEmailForRegion(String region) {
        List<String> emailRecipients = new ArrayList<String>();

        if (region == null || region.trim().length() == 0) {
            emailRecipients.add(settingsFacade.getProperty(MotechConstants.DEFAULT_EMAIL));
            return emailRecipients;
        }

        Type type = new TypeToken<List<EmailEntry>>() {
        }.getType();

        InputStream emailSettingsStream = settingsFacade.getRawConfig(MotechConstants.EMAIL_SETTINGS_FILE_NAME);

        if (emailSettingsStream == null) {
            logger.warn("No e-mail settings file found");
            return emailRecipients;
        }

        List<EmailEntry> emails = (List<EmailEntry>) motechJsonReader.readFromStream(emailSettingsStream, type);

        for (EmailEntry entry : emails) {

            if (entry.getRegion().trim().toLowerCase().equals(region.trim().toLowerCase()) || entry.getRegion().equals("all")) {
                emailRecipients.add(entry.getEmailAddress());
            }
        }

        logger.warn("Returning recipients ..." + " " + emailRecipients.size() );
        return emailRecipients;
    }
}
