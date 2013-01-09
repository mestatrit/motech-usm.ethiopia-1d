package org.motech.location.repository.domain;

import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.ektorp.support.TypeDiscriminator;
import org.motech.location.repository.dao.IdentifierDeserializer;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Location'")
public class Location extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    @JsonDeserialize(using = IdentifierDeserializer.class)
    private MotechLocationIdentifier motechId;
    
    private List<CustomLocationIdentifier> customIdentifiers;

    private Map<String, String> attributes;

    private List<String> path;

    public MotechLocationIdentifier getMotechId() {
        return motechId;
    }

    public void setMotechId(MotechLocationIdentifier motechId) {
        this.motechId = motechId;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<CustomLocationIdentifier> getCustomIdentifiers() {
        return customIdentifiers;
    }

    public void setCustomIdentifiers(
            List<CustomLocationIdentifier> customIdentifiers) {
        this.customIdentifiers = customIdentifiers;
    }
}
