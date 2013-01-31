package org.motechproject.commcarestdemo.vxml;

import org.springframework.stereotype.Component;

@Component
public class VxmlCalculator {

    public String calculateVxmlLocation(String messageName) {
        String vxmlLoc = "http://webhosting.voxeo.net/150494/www/";
        if (messageName.matches("providerMessageDue")) {
            vxmlLoc += "providerMessageDue.xml";
        }
        if (messageName.matches("patientMessageDue")) {
            vxmlLoc += "patientMessageDue.xml";
        }
        if (messageName.matches("patientMessageMissed")) {
            vxmlLoc += "patientMessageMissed.xml";
        }
        if (messageName.matches("providerMessageMissed")) {
            vxmlLoc += "providerMessageMissed.xml";
        } 
        return vxmlLoc;
    }

}
