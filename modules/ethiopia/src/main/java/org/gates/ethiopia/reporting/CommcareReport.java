package org.gates.ethiopia.reporting;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

public class CommcareReport implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String locationId;
    private String locationName;
    private DateTime startDate;
    private DateTime endDate;
    private Map<String, String> reportingValues = new HashMap<String, String>();
    private int numberOfForms;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public Map<String, String> getReportingValues() {
        return reportingValues;
    }

    public void setReportingValues(Map<String, String> reportingValues) {
        this.reportingValues = reportingValues;
    }

    public int getNumberOfForms() {
        return numberOfForms;
    }

    public void setNumberOfForms(int numberOfForms) {
        this.numberOfForms = numberOfForms;
    }
}
