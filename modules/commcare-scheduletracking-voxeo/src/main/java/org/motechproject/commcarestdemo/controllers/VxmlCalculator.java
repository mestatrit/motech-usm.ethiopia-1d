package org.motechproject.commcarestdemo.controllers;

import org.springframework.stereotype.Component;

@Component
public class VxmlCalculator {

    public String calculateVxmlLocation(String messageName) {
        String vxmlLoc = "http://webhosting.voxeo.net/92594/www/";
        if (messageName.matches("IVRVisitToPatient")) {
            vxmlLoc += "providerMessageDue.xml";
        }
        if (messageName.matches("IVRVisitToProvider")) {
            vxmlLoc += "patientMessageDue.xml";
        }
        if (messageName.matches("IVRMissedVisitToPatient")) {
            vxmlLoc += "patientMessageMissed.xml";
        }
        if (messageName.matches("IVRMissedVisitToProvider")) {
            vxmlLoc += "providerMessageMissed.xml";
        } else {
            return null;
        }
        return vxmlLoc;
    }

    /*
     * @RequestMapping(value = "/vxml", method = RequestMethod.GET) public
     * ModelAndView handleRequest(HttpServletRequest request,
     * HttpServletResponse response) { logger.info("Generate VXML");
     * 
     * response.setContentType("text/xml");
     * response.setCharacterEncoding("UTF-8");
     * 
     * ModelAndView mav = new ModelAndView();
     * 
     * mav.addObject("audioPathBase",
     * "http://130.111.132.59:8080/motech-platform-server/module");
     * 
     * mav.setViewName("vxml");
     * 
     * return mav; }
     */
}
