package org.gates.ethiopia.scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.gates.ethiopia.constants.EventConstants;
import org.gates.ethiopia.constants.MotechConstants;
import org.gates.ethiopia.service.GatesEthiopiaMailService;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.motechproject.eventlogging.service.EventQueryService;
import org.motechproject.model.DayOfWeek;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduler {

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    private static final long REPEAT_INTERVAL_IN_MILLIS = 60000;

    private static final int START_DAY = DayOfWeek.Monday.getValue();

    private static final int HOUR_OF_DAY = 16;

    private static final int MINUTE_OF_HOUR = 16;

    private static final int PREVIOUS_DAYS_TO_CHECK = 7;

    @Autowired
    private MotechSchedulerService schedulerService;

    @Autowired
    private EventQueryService<CouchEventLog> eventQueryService;

    @Autowired
    private GatesEthiopiaMailService mailService;

    @Autowired
    @Qualifier(value = "regionEmailConfiguration")
    private Properties regionEmailConfiguration;

    @Autowired
    @Qualifier(value = "emailSettings")
    private Properties emailSettings;

    @Autowired
    private SettingsFacade settingsFacade;

    @PostConstruct
    public void initiateJob() {

        MotechEvent combineEmails = new MotechEvent(EventConstants.COMBINE_EMAILS);

        RepeatingSchedulableJob job = new RepeatingSchedulableJob();

        DateTime startTime = new DateTime().withDayOfWeek(START_DAY).withHourOfDay(HOUR_OF_DAY).withMinuteOfHour(MINUTE_OF_HOUR);

        logger.info("Starting aggregate e-mail job at: " + startTime.toDate());

        job.setStartTime(startTime.toDate());
        job.setRepeatIntervalInMilliSeconds((REPEAT_INTERVAL_IN_MILLIS));
        job.setMotechEvent(combineEmails);

        schedulerService.safeScheduleRepeatingJob(job);
    }

    @MotechListener(subjects = EventConstants.COMBINE_EMAILS)
    public void handleJob(MotechEvent event) {
        Map<String, List<CouchEventLog>> emailBuilderMap = new HashMap<String, List<CouchEventLog>>();

        List<CouchEventLog> logs = eventQueryService.getAllEventsBySubject(EventConstants.LATE_EVENT);

        for (CouchEventLog log : logs) {

            DateTime logTime = log.getTimeStamp();

            String region = (String) log.getParameters().get(MotechConstants.REGION);

            if (region == null || region.trim().length() == 0) {
                region = MotechConstants.DEFAULT_EMAIL;
            }

            region = region.trim().toLowerCase();

            if (logTime.isAfter(DateTime.now().minusDays(PREVIOUS_DAYS_TO_CHECK))) {
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
        constructEmails(emailBuilderMap);
    }

    private void constructEmails(Map<String, List<CouchEventLog>> emailBuilderMap) {
        for (String region : emailBuilderMap.keySet()) {
            List<CouchEventLog> logs = emailBuilderMap.get(region);
            buildEmail(region, logs);
        }
    }

    private void buildEmail(String region, List<CouchEventLog> logs) {
        String recipient = getEmailForRegion(region);
        StringBuilder stringBuilder = new StringBuilder("The following facilities have not submitted a report within the last seven days: \n\n");

        for (int i = 0; i < logs.size(); i++) {
            stringBuilder.append((i + 1) + "." + "  Woreda: " + logs.get(i).getParameters().get(MotechConstants.WOREDA) + "   |  Facility: " + logs.get(i).getParameters().get(MotechConstants.FACILITY_NAME) + "\n");
        }      

        mailService.sendAggregateEmailReminder(recipient, stringBuilder.toString(), settingsFacade.getProperty(MotechConstants.EMAIL_SUBJECT).replace(MotechConstants.REGION_PLACEHOLDER, region));
    }

    private String getEmailForRegion(String region) {
        if (region == null || region.trim().length() == 0) {
            return settingsFacade.getProperty(MotechConstants.DEFAULT_EMAIL);
        }

        return settingsFacade.getProperty(region.trim().toLowerCase());
    }
}
