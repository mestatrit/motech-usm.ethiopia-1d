package org.motechproject.commcarestdemo.listeners;

import org.motechproject.commcarestdemo.eventhandlers.DueVisitHandler;
import org.motechproject.commcarestdemo.eventhandlers.EarliestVisitHandler;
import org.motechproject.commcarestdemo.eventhandlers.LateVisitHandler;
import org.motechproject.commcarestdemo.eventhandlers.MaxVisitHandler;
import org.motechproject.commcarestdemo.util.DemoConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MilestoneEventsListener {
    
    @Autowired
    private LateVisitHandler lateVisitHandler;
    
    @Autowired
    private DueVisitHandler dueVisitHandler;
    
    @Autowired
    private EarliestVisitHandler earliestVisitHandler;
    
    @Autowired
    private MaxVisitHandler maxVisitHandler;
    
    @MotechListener(subjects = EventSubjects.MILESTONE_ALERT)
    public void handleMilestoneEvent(MotechEvent event) {
        MilestoneEvent milestoneEvent = new MilestoneEvent(event);
        
        if (DemoConstants.SCHEDULE_NAME.equals(milestoneEvent.getScheduleName())) {
            switch(milestoneEvent.getWindowName()) {
                case "earliest": earliestVisitHandler.handle(milestoneEvent); break;
                case "due": dueVisitHandler.handle(milestoneEvent); break;
                case "late": lateVisitHandler.handle(milestoneEvent); break;
                case "max": maxVisitHandler.handle(milestoneEvent); break;
                default: break;
            }
        }
    }

}
