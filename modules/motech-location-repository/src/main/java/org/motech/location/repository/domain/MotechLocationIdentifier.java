package org.motech.location.repository.domain;

import java.util.Map;

public class MotechLocationIdentifier {
    
    private String externalId;

    public MotechLocationIdentifier(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getIdentifierName() {
        return "motech_location_id";
    }

    public boolean identifiedBy(Location provider) {
        // TODO Auto-generated method stub
        return false;
    }

    public Map<String, String> getIdentifierMap() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getIdentity() {
        return externalId;
    }
}
