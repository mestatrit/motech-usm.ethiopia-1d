package org.motechproject.mapper;

import java.util.HashMap;

public class CaseElementMapping {

    private String conceptName;
    private String caseElementName;
    private HashMap<String, String> obsValuesToCaseValue;

    public CaseElementMapping(String conceptName, String caseElementName) {
        this.conceptName = conceptName;
        this.caseElementName = caseElementName;
        obsValuesToCaseValue = new HashMap<>();
    }

    public void addFieldValue(String obsValue, String caseValue) {
        obsValuesToCaseValue.put(obsValue.toLowerCase(), caseValue);
    }

    public boolean handles(String conceptName2) {
        return conceptName.equalsIgnoreCase(conceptName2);
    }

    public String translateValue(String string) {
        String value = obsValuesToCaseValue.get(string.toLowerCase());
        return value == null ? string : value;
    }

    public String getCaseElement() {
        return caseElementName;
    }

}
