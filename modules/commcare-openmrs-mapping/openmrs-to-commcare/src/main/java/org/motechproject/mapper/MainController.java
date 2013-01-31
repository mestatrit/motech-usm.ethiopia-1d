package org.motechproject.mapper;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.openmrs.atomfeed.events.EventDataKeys;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

    @Autowired
    private EventRelay eventRelay;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getIndex() {
        return new ModelAndView("index");
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView postData(@RequestBody MultiValueMap<String, String> map) {
        String eventToRaise = map.getFirst("eventToRaise");
        String uuid = map.getFirst("objectUuid");
        MotechEvent event = null;
        if ("obsUpdate".equals(eventToRaise)) {
            event = new MotechEvent(EventSubjects.OBSERVATION_UPDATE);
            event.getParameters().put(EventDataKeys.UUID, uuid);
        } else {
            event = new MotechEvent(EventSubjects.ENCOUNTER_CREATE);
            event.getParameters().put(EventDataKeys.UUID, uuid);
        }
        eventRelay.sendEventMessage(event);

        return new ModelAndView("index");
    }
}
