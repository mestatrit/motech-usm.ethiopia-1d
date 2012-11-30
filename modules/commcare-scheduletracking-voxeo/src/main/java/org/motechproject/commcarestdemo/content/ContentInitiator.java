package org.motechproject.commcarestdemo.content;

import java.io.InputStream;

import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.springframework.beans.factory.annotation.Autowired;

public class ContentInitiator {

    @Autowired
    private CMSLiteService cmsLiteService;

    public void loadContent() throws CMSLiteException {

        InputStream inputStreamToResource1 = this.getClass().getResourceAsStream("/providerMessage.wav");

        // This maps to "/cmsliteapi/stream/en/IVRVisitToProvider"
        StreamContent providerMessageDueIVR = new StreamContent("en", "IVRVisitToProvider", inputStreamToResource1, "checksum1", "audio/wav");
        cmsLiteService.addContent(providerMessageDueIVR);        
        // This points to the Vxml location for the Provider Due Message
        StringContent providerMessageDueVxml = new StringContent("en", "providerMessageDueVxml", "/vxmlContent/providerMessageDue.xml");
        cmsLiteService.addContent(providerMessageDueVxml);
                
 /*       //This maps to "/cmsliteapi/stream/en/IVRVisitToPatient"
        StreamContent patientMessageDueIVR = new StreamContent("en", "IVRVisitToPatient", inputStreamToResource1, "checksum1", "audio/wav");
        cmsLiteService.addContent(patientMessageDueIVR);       
        // This points to the Vxml location for the Patient Due Message
        StringContent patientMessageDueVxml = new StringContent("en", "patientMessageDueVxml", "patientMessageDue.xml");
        cmsLiteService.addContent(patientMessageDueVxml);
                      
        //This maps to "/cmsliteapi/stream/en/IVRMissedVisitToProvider"
        StreamContent providerMessageMissedIVR = new StreamContent("en", "IVRMissedVisitToProvider", inputStreamToResource1, "checksum1", "audio/wav");
        cmsLiteService.addContent(providerMessageMissedIVR);        
        // This points to the Vxml location for the Provider Missed Message
        StringContent providerMessageMissedVxml = new StringContent("en", "providerMessageMissedVxml", "providerMessageMissed.xml");
        cmsLiteService.addContent(providerMessageMissedVxml);
        
        //This maps to "/cmsliteapi/stream/en/IVRMissedVisitToPatient"
        StreamContent patientMessageMissedIVR = new StreamContent("en", "IVRMissedVisitToPatient", inputStreamToResource1, "checksum1", "audio/wav");
        cmsLiteService.addContent(patientMessageMissedIVR);        
        // This points to the Vxml location for the Patient Missed Message
        StringContent patientMessageMissedVxml = new StringContent("en", "patientMessageMissedVxml", "patientMessageMissed.xml");
        cmsLiteService.addContent(patientMessageMissedVxml);*/

        
     

    }
}