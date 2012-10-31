package org.motechproject.mapper;

import java.util.HashMap;

public class CaseElementMapping {

    private boolean required;
    private String conceptName;
    private String caseElementName;
    private HashMap<String, String> translationMappings;

    public CaseElementMapping() {
        translationMappings = new HashMap<String, String>();
    }

    public CaseElementMapping(String conceptName, String caseElementName) {
        this.conceptName = conceptName;
        translationMappings = new HashMap<String, String>();
    }

    public void addFieldValue(String obsValue, String caseValue) {
        translationMappings.put(obsValue.toLowerCase(), caseValue);
    }

    public boolean handles(String conceptName2) {
        return conceptName.equalsIgnoreCase(conceptName2);
    }

    public String translateValue(String string) {
        String value = translationMappings.get(string.toLowerCase());
        return value == null ? string : value;
    }

    public String getCaseElement() {
        return caseElementName;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getCaseElementName() {
        return caseElementName;
    }

    public void setCaseElementName(String caseElementName) {
        this.caseElementName = caseElementName;
    }

    public HashMap<String, String> getObsValuesToCaseValue() {
        return translationMappings;
    }

    public void setObsValuesToCaseValue(HashMap<String, String> obsValuesToCaseValue) {
        this.translationMappings = obsValuesToCaseValue;
    }
}
