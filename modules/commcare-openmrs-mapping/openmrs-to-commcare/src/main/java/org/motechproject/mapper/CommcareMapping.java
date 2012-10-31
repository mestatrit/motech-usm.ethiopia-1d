package org.motechproject.mapper;

import java.util.List;
import java.util.Map;

public class CommcareMapping {

    private String matchOnEncounterType;
    private String type;
    private Map<String, String> idScheme;
    private List<CaseElementMapping> mappings;

    public String getMatchOnEncounterType() {
        return matchOnEncounterType;
    }

    public void setMatchOnEncounterType(String matchOnEncounterType) {
        this.matchOnEncounterType = matchOnEncounterType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getIdScheme() {
        return idScheme;
    }

    public void setIdScheme(Map<String, String> idScheme) {
        this.idScheme = idScheme;
    }

    public List<CaseElementMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<CaseElementMapping> mappings) {
        this.mappings = mappings;
    }
}
