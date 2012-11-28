package org.motech.provider.repository.domain;

import java.util.Map;

public class CommcareProviderIdentifier extends ProviderIdentifier {

    private String userId;
    private String domain;
    private String username;

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
        if (identifierToCompare instanceof CommcareProviderIdentifier) {
            CommcareProviderIdentifier identifier = (CommcareProviderIdentifier) identifierToCompare;
            if (this.getUserId().equals(identifier.getUserId()) || (this.getDomain().equals(identifier.getDomain()) && this.getUsername().equals(identifier.getUsername()))) {
                return true;
            }
        }
        return false;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getIdentifierName() {
        return "commcare_provider_id";
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
