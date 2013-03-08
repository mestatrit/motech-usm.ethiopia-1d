package org.motechproject.openmrs.ws.resource.model;

import java.util.List;

public class ProviderListResult {

    private List<Provider> results;

    public List<Provider> getResults() {
        return results;
    }

    public void setResults(List<Provider> results) {
        this.results = results;
    }
}
