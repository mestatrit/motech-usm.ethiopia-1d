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

        InputStream inputStreamToResource1 = this.getClass().getResourceAsStream("/providerMessageDue.wav");

        // This maps to "/cmsliteapi/stream/en/VisitToProviderIVR"
        StreamContent providerMessageDueIVR = new StreamContent("en", "VisitToProviderIVR", inputStreamToResource1, "checksum1", "audio/wav");
        cmsLiteService.addContent(providerMessageDueIVR);        
        // This points to the Vxml location for the Provider Due Message
        StringContent providerMessageDueVxml = new StringContent("en", "providerMessageDue", "providerMessageDue.xml");
        cmsLiteService.addContent(providerMessageDueVxml);
       
        InputStream inputStreamToResource2 = this.getClass().getResourceAsStream("/patientMessageDue.wav");
        
        //This maps to "/cmsliteapi/stream/en/VisitToPatientIVR"
        StreamContent patientMessageDueIVR = new StreamContent("en", "VisitToPatientIVR", inputStreamToResource2, "checksum1", "audio/wav");
        cmsLiteService.addContent(patientMessageDueIVR);       
        // This points to the Vxml location for the Patient Due Message
        StringContent patientMessageDueVxml = new StringContent("en", "patientMessageDue", "patientMessageDue.xml");
        cmsLiteService.addContent(patientMessageDueVxml);
                              
        InputStream inputStreamToResource3 = this.getClass().getResourceAsStream("/providerMessageMissed.wav");
        
        //This maps to "/cmsliteapi/stream/en/MissedVisitToProviderIVR"
        StreamContent providerMessageMissedIVR = new StreamContent("en", "MissedVisitToProviderIVR", inputStreamToResource3, "checksum1", "audio/wav");
        cmsLiteService.addContent(providerMessageMissedIVR);        
        // This points to the Vxml location for the Provider Missed Message
        StringContent providerMessageMissedVxml = new StringContent("en", "providerMessageMissed", "providerMessageMissed.xml");
        cmsLiteService.addContent(providerMessageMissedVxml);
        
        InputStream inputStreamToResource4 = this.getClass().getResourceAsStream("/patientMessageMissed.wav");
        
        //This maps to "/cmsliteapi/stream/en/MissedVisitToPatientIVR"
        StreamContent patientMessageMissedIVR = new StreamContent("en", "MissedVisitToPatientIVR", inputStreamToResource4, "checksum1", "audio/wav");
        cmsLiteService.addContent(patientMessageMissedIVR);        
        // This points to the Vxml location for the Patient Missed Message
        StringContent patientMessageMissedVxml = new StringContent("en", "patientMessageMissed", "patientMessageMissed.xml");
        cmsLiteService.addContent(patientMessageMissedVxml);

    }
}