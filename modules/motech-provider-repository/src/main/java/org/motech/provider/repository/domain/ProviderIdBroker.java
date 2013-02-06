package org.motech.provider.repository.domain;

import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mrs.domain.Provider;

@TypeDiscriminator("doc.type === 'Provider'")
public class ProviderIdBroker extends MotechBaseDataObject {

    private static final long serialVersionUID = 1L;

    private String motechId;

    private List<CustomProviderIdentifier> identifiers;

    private List<String> locationIdentities;
    
    @JsonIgnore
    private Provider mrsProvider;

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public List<CustomProviderIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<CustomProviderIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public List<String> getLocationIdentities() {
        return locationIdentities;
    }

    public void setLocationIdentities(List<String> locationIdentities) {
        this.locationIdentities = locationIdentities;
    }

    public Provider getMrsProvider() {
        return mrsProvider;
    }

    public void setMrsProvider(Provider mrsProvider) {
        this.mrsProvider = mrsProvider;
    }
}
