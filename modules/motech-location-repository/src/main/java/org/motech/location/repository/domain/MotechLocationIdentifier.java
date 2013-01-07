package org.motech.location.repository.domain;

import java.util.Map;

public class MotechLocationIdentifier extends LocationIdentifier {
    
    private String externalId;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String getIdentifierName() {
        return "motech_location_id";
    }

    @Override
    public boolean identifiedBy(Location provider) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<String, String> getIdentifierMap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean equals(Object identifierToCompare) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getIdentity() {
        return externalId;
    }
}
