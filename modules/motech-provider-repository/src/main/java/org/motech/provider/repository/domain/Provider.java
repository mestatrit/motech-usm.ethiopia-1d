package org.motech.provider.repository.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.motech.provider.repository.dao.IdentifierDeserializer;
import org.motech.provider.repository.dao.GeneralIdentifierDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.mrs.model.MRSPerson;

@TypeDiscriminator("doc.type === 'Provider'")
public class Provider extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    private MRSPerson person;

    @JsonDeserialize(using = IdentifierDeserializer.class)
    private MotechIdentifier motechId;

    @JsonDeserialize(using = GeneralIdentifierDeserializer.class)
    private List<ProviderIdentifier> identifiers;

    private List<String> locationIdentities;

    public List<ProviderIdentifier> getEquivalentIdentifiers(ProviderIdentifier queryIdentifier) {
        if (this.identifiers.contains(queryIdentifier)) {
            return identifiers;
        }
        return Collections.emptyList();
    }


    public List<ProviderIdentifier> getEquivalentIdentifierByType(String identifierType, ProviderIdentifier queryIdentifier) {
        List<ProviderIdentifier> equivalentIdentifiers = new ArrayList<ProviderIdentifier>();
        if (this.identifiers.contains(queryIdentifier)) {
            for (ProviderIdentifier identifier : identifiers) {
                if (identifier.getIdentifierName().equals(identifierType)) {
                    equivalentIdentifiers.add(identifier);
                }
            }
        }
        return equivalentIdentifiers;
    }

    public MotechIdentifier getMotechId() {
        return motechId;
    }

    public void setMotechId(MotechIdentifier motechId) {
        this.motechId = motechId;
    }

    public List<ProviderIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<ProviderIdentifier> identifiers) {
        this.identifiers = identifiers;
    }


    public List<String> getLocationIdentities() {
        return locationIdentities;
    }


    public void setLocationIdentities(List<String> locationIdentities) {
        this.locationIdentities = locationIdentities;
    }


    public MRSPerson getPerson() {
        return person;
    }


    public void setPerson(MRSPerson person) {
        this.person = person;
    }
}
