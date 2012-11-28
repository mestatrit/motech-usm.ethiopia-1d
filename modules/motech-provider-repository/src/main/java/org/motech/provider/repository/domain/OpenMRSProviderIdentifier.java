package org.motech.provider.repository.domain;

import java.util.Map;

public class OpenMRSProviderIdentifier extends ProviderIdentifier {
    
    private String userName;
    private String uuid;

    @Override
    public boolean identifiedBy(Provider provider) {
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
        if (identifierToCompare instanceof OpenMRSProviderIdentifier) {
            OpenMRSProviderIdentifier identifier = (OpenMRSProviderIdentifier) identifierToCompare;
            if (this.getUuid().equals(identifier.getUuid())) {
                return true;
            }
        }
        return false;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuuid) {
        this.uuid = uuid;
    }

    @Override
    public String getIdentifierName() {
        return "openmrs_provider_id";
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
