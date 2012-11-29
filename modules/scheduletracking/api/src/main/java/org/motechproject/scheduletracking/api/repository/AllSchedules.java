package org.motechproject.scheduletracking.api.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.ScheduleFactory;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllSchedules extends MotechBaseRepository<ScheduleRecord> {

    private ScheduleFactory scheduleFactory;
    private PlatformSettingsService platformSettingsService;

    @Autowired
    public AllSchedules(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db, ScheduleFactory scheduleFactory,
                        PlatformSettingsService platformSettingsService) {
        super(ScheduleRecord.class, db);
        this.scheduleFactory = scheduleFactory;
        this.platformSettingsService = platformSettingsService;
    }

    @View(name = "by_name", map = "function(doc) { if(doc.type === 'ScheduleRecord') emit(doc.name); }")
    public Schedule getByName(String name) {
        List<ScheduleRecord> records = queryView("by_name", name);
        if (records.isEmpty()) {
            return null;
        }
        return scheduleFactory.build(records.get(0), platformSettingsService.getPlatformLocale());
    }

    public List<Schedule> getAllSchedules() {
        List<ScheduleRecord> records = getAll();
        List<Schedule> schedules = new ArrayList<Schedule>();

        for (ScheduleRecord record : records) {
            schedules.add(scheduleFactory.build(record, platformSettingsService.getPlatformLocale()));
        }

        return schedules;
    }

    public ScheduleRecord getRecordByName(String name) {
        List<ScheduleRecord> records = queryView("by_name", name);
        if (records.isEmpty()) {
            return null;
        }
        
        return records.get(0);
    }

    public void updateRecord(ScheduleRecord newSchedule) {
        if (getRecordByName(newSchedule.name()) != null) {
            this.update(newSchedule);
        } else {
            this.add(newSchedule);
        }
    }

    public void deleteRecord(ScheduleRecord recordToDelete) {
        this.remove(recordToDelete);        
    }
}
