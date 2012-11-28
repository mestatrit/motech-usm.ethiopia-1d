package org.motech.provider.repository.domain;

import java.util.Map;

public abstract class ProviderIdentifier {

    public abstract String getIdentifierName();
    
    public abstract boolean identifiedBy(Provider provider);
    
    public abstract Map<String, String> getIdentifierMap();
    
    public abstract boolean equals(Object identifierToCompare);
    
    public abstract int hashCode();
}
