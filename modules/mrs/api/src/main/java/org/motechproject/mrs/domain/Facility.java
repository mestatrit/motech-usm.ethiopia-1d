package org.motechproject.mrs.domain;

public interface Facility {

    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getCountry();

    void setCountry(String country);

    String getRegion();

    void setRegion(String region);

    String getCountyDistrict();

    void setCountyDistrict(String countyDistrict);

    String getStateProvince();

    void setStateProvince(String stateProvince);
}
