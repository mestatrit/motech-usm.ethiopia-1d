package org.motech.provider.repository.domain;

import java.util.Map;

public class MotechIdentifier extends ProviderIdentifier {
    
    private String externalId;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String getIdentifierName() {
        return "motech_provider_id";
    }

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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
