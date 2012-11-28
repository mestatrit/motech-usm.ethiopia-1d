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

        // This points to the actual audio/wav file:
        StreamContent providerMessageIVR = new StreamContent("en", "providerMessage", inputStreamToResource1, "checksum1", "audio/wav");
        cmsLiteService.addContent(providerMessageIVR);

        // This points to the Vxml location
        StringContent providerMessageVxml = new StringContent("en", "messageIVR", "providerMessage.wav");
        cmsLiteService.addContent(providerMessageVxml);

        StringContent providerMessageSMS = new StringContent("en", "message1", "Your patient has missed a visit. Please contact your "
                + "patient and be sure that he or she is aware of this.");
        cmsLiteService.addContent(providerMessageSMS);
    }
}