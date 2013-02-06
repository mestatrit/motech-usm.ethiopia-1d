package org.motech.location.repository.domain;

import java.util.HashMap;
import java.util.Map;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'LocationIdentifierType'")
public class LocationIdentifierType extends MotechBaseDataObject {

    private String identifierName;

    private Map<String, Boolean> identifyingProperties = new HashMap<String, Boolean>();

    public String getIdentifierName() {
        return identifierName;
    }

    public void setIdentifierName(String identifierName) {
        this.identifierName = identifierName;
    }

    public Map<String, Boolean> getIdentifyingProperties() {
        return identifyingProperties;
    }

    public void setIdentifyingProperties(Map<String, Boolean> identifyingProperties) {
        this.identifyingProperties = identifyingProperties;
    }
}
