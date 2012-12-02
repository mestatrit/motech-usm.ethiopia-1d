package org.motech.location.repository.domain;

import java.util.Map;

public class OpenMRSLocationIdentifier extends LocationIdentifier {
    
    private String facilityName;
    private String uuid;

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
        if (identifierToCompare instanceof OpenMRSLocationIdentifier) {
            OpenMRSLocationIdentifier identifier = (OpenMRSLocationIdentifier) identifierToCompare;
            if (this.getUuid().equals(identifier.getUuid())) {
                return true;
            }
        }
        return false;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getIdentifierName() {
        return "openmrs_location_id";
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getIdentity() {
        return uuid;
    }
}
