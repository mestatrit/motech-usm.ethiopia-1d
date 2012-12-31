package org.motech.location.repository.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.ektorp.support.TypeDiscriminator;
import org.motech.location.repository.dao.LocationIdentifierDeserializer;
import org.motech.location.repository.dao.IdentifierDeserializer;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Location'")
public class Location extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;
    
    private List<Location> childNodes = new ArrayList<Location>();

    @JsonDeserialize(using = IdentifierDeserializer.class)
    private MotechLocationIdentifier motechId;

    @JsonDeserialize(using = LocationIdentifierDeserializer.class)
    private List<LocationIdentifier> identifiers;

    private Map<String, String> attributes;

    public List<LocationIdentifier> getEquivalentIdentifiers(LocationIdentifier queryIdentifier) {
        if (this.identifiers.contains(queryIdentifier)) {
            return identifiers;
        }
        return Collections.emptyList();
    }

    public List<LocationIdentifier> getEquivalentIdentifierByType(String identifierType, LocationIdentifier queryIdentifier) {
        List<LocationIdentifier> equivalentIdentifiers = new ArrayList<LocationIdentifier>();
        if (this.identifiers.contains(queryIdentifier)) {
            for (LocationIdentifier identifier : identifiers) {
                if (identifier.getIdentifierName().equals(identifierType)) {
                    equivalentIdentifiers.add(identifier);
                }
            }
        }
        return equivalentIdentifiers;
    }

    public MotechLocationIdentifier getMotechId() {
        return motechId;
    }

    public void setMotechId(MotechLocationIdentifier motechId) {
        this.motechId = motechId;
    }

    public List<LocationIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<LocationIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<Location> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<Location> childNodes) {
        this.childNodes = childNodes;
    }
    
    public void addChildNode(Location childNode) {
        this.childNodes.add(childNode);
    }
}
