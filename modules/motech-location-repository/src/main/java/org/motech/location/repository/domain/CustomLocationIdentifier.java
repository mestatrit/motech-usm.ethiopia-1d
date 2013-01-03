package org.motech.location.repository.domain;

import java.util.Map;

public class CustomLocationIdentifier  {

    private String identifierType;

    private Map<String, String> identifyingProperties;
    
    public CustomLocationIdentifier(String identifierType, Map<String, String> identifyingProperties) {
        this.identifierType = identifierType;
        this.identifyingProperties = identifyingProperties;
    }
    
    public String getIdentity() {
        return "identity";
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
