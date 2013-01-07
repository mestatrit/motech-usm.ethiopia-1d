package org.motech.location.repository.domain;

import java.util.Map;

public abstract class LocationIdentifier {

    public abstract String getIdentifierName();
    
    public abstract boolean identifiedBy(Location location);
    
    public abstract Map<String, String> getIdentifierMap();
    
    public abstract boolean equals(Object identifierToCompare);
    
    public abstract int hashCode();
    
    public abstract String getIdentity();
}
