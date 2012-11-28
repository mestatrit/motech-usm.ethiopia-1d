package org.motechproject.commcarestdemo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchedulesUtil {
    
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    
    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");
    
    @PostConstruct
    public void init() {
        
        //scheduleTrackingService.getScheduleByName(DemoConstants.SCHEDULE_NAME);
        
        InputStream is = getClass().getClassLoader().getResourceAsStream(DemoConstants.SCHEDULE_FILE_NAME);
        
        StringWriter writer = new StringWriter();

        try {
            IOUtils.copy(is, writer, "UTF-8");
        } catch (IOException e) {
            logger.warn("Unable to read schedule: " + e.getMessage());
        }

        logger.warn("Adding schedule... ");
        
        scheduleTrackingService.updateSchedule(writer.toString());
    }

}
