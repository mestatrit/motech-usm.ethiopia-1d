package org.motech.provider.repository.domain;

import java.util.HashMap;
import java.util.Map;

public class CustomProviderIdentifier  {

    private String identifierType;

    private Map<String, String> identifyingProperties = new HashMap<String, String>();

    public CustomProviderIdentifier() { }

    public CustomProviderIdentifier(String identifierType, Map<String, String> identifyingProperties) {
        this.identifierType = identifierType;
        this.identifyingProperties = identifyingProperties;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public Map<String, String> getIdentifyingProperties() {
        return identifyingProperties;
    }

    public void setIdentifyingProperties(Map<String, String> identifyingProperties) {
        this.identifyingProperties = identifyingProperties;
    }

}
