package org.motech.provider.repository.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Provider'")
public class Provider extends MotechBaseDataObject {
    
    private static final long serialVersionUID = 1L;

    private MotechIdentifier motechId;
    
    private List<ProviderIdentifier> identifiers;
    
    public List<ProviderIdentifier> getEquivalentIdentifiers(ProviderIdentifier queryIdentifier) {
        if (this.identifiers.contains(queryIdentifier)) {
            return identifiers;
        }
        return Collections.emptyList();
    }

    
    public List<? extends ProviderIdentifier> getEquivalentIdentifierByType(String identifierType, ProviderIdentifier queryIdentifier) {
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
}
