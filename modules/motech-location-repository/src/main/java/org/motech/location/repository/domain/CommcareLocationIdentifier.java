package org.motech.location.repository.domain;

import java.util.Map;

public class CommcareLocationIdentifier extends LocationIdentifier {

    private String locationFieldValue;
    private String locationid;
    private String domain;

    @Override
    public boolean identifiedBy(Location location) {
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
        if (identifierToCompare instanceof CommcareLocationIdentifier) {
            CommcareLocationIdentifier identifier = (CommcareLocationIdentifier) identifierToCompare;
            if (this.getLocationid().equals(identifier.getLocationid())) {
                return true;
            }
        }
        return false;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getIdentifierName() {
        return "commcare_location_id";
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getLocationFieldValue() {
        return locationFieldValue;
    }

    public void setLocationFieldValue(String locationFieldValue) {
        this.locationFieldValue = locationFieldValue;
    }

    public String getLocationid() {
        return locationid;
    }

    public void setLocationid(String locationid) {
        this.locationid = locationid;
    }

    @Override
    public String getIdentity() {
       return locationid;
    }
}
