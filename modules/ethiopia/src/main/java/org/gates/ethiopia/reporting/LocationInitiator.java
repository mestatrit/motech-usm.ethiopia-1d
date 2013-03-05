package org.gates.ethiopia.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.gates.ethiopia.constants.CommcareConstants;
import org.joda.time.DateTime;
import org.motech.location.repository.domain.CustomLocationIdentifier;
import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationIdentifierType;
import org.motech.location.repository.domain.LocationValidationException;
import org.motech.location.repository.service.LocationIdentifierService;
import org.motech.location.repository.service.LocationRepositoryService;
import org.motech.provider.repository.domain.CustomProviderIdentifier;
import org.motech.provider.repository.domain.ProviderIdBroker;
import org.motech.provider.repository.service.ProviderRepositoryService;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.service.CommcareFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationInitiator {

    public static final String REGION = "region";

    public static final String WOREDA = "woreda";

    public static final String FACILITY_NAME = "facility_name";

    public static final String FACILITY = "facility";

    public static final String HEW_NAME = "hew_name";

    public static final String HEW_MOBILE_NUMBER = "hew_mobile_number";

    public static final String COMMCARE_HEW = "HEW";

    private static final String COMMCARE_CASE = "COMMCARE_CASE";

    private static final String CASE_ID = "case_id";

    @Autowired
    private LocationRepositoryService locationService;

    @Autowired
    private LocationIdentifierService locationIdService;

    @Autowired
    private ProviderRepositoryService providerService;

    @Autowired
    private CommcareFormService formService;

    @Autowired
    private CommcareCaseService caseService;

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    public void initialize() {
        logger.warn("initializing...");

        initiateIdentifierTypes();

        List<CaseInfo> hewCases = caseService.getAllCasesByType("hew");

        logger.warn("# of hew cases: " + hewCases.size());

        if (hewCases != null) {
            for (CaseInfo hewCase : hewCases) {
                addOrUpdateLocation(hewCase);
            }
        }
        DateTime date1 = DateTime.now().minusWeeks(52);
        DateTime date2 = DateTime.now();
        
        testCaseElement();

        allFacilitiesReport("123", date1, date2);

        //        outputLocations();

        //        checkNumberOfForms();
    }

    private void testCaseElement() {
        CommcareForm form = formService.retrieveForm("d7ebcab3-3714-4b01-9761-8df63e04ed6a");
        if (form != null) {
            FormValueElement caseElement = form.getCaseElement();
            if (caseElement != null) {
                logger.warn("Case element not null");
            } else {
                logger.warn("NULL");
            }
        } else {
            logger.warn("FORM NULL");
        }
        logger.warn("ID: " + form.getId());
    }

    private void allFacilitiesReport(String locationId, DateTime date1, DateTime date2) {
        if (locationId == null) {
            List<CommcareReport> combinedRegionReports = new ArrayList<CommcareReport>();
            List<Location> regions = locationService.getAllLocationsByType("region");

            for (Location region : regions) {
                CommcareReport regionReport = tallySubFacilities(region.getMotechId(), date1, date2);
                regionReport.setLocationId(region.getMotechId());
                regionReport.setStartDate(date1);
                regionReport.setEndDate(date2);
                regionReport.setLocationName(region.getCustomIdentifiers().get(0).getIdentifyingProperties().get(REGION));
                logger.warn("TOTAL FOR A REGION: " + FormReporter.generateJsonString(regionReport));
                combinedRegionReports.add(regionReport);
            }

            CommcareReport totalReport = combineReports(combinedRegionReports);
            totalReport.setStartDate(date1);
            totalReport.setStartDate(date2);
            totalReport.setLocationName("All regions");
            logger.warn("TOTAL FROM ALL REGIONS: " + FormReporter.generateJsonString(totalReport));
        }
    }

    private CommcareReport tallySubFacilities(String parentLocation, DateTime date1, DateTime date2) {
        List<Location> facilities = locationService.getAllChildrenByType(parentLocation, FACILITY);
        List<CommcareReport> combinedFacilityReports = new ArrayList<CommcareReport>();
        for (Location facility : facilities) {
            List<CommcareReport> providerReports = new ArrayList<CommcareReport>();
            List<ProviderIdBroker> providers = providerService.getProvidersByLocationId(facility.getMotechId());
            for (ProviderIdBroker provider : providers) {
                String caseId = provider.getIdentifiers().get(1).getIdentifyingProperties().get(CASE_ID);
                CaseInfo caseInfo = caseService.getCaseByCaseId(caseId);
                CommcareReport report = tallyForms(caseInfo.getXformIds(), date1, date2, facility);
                providerReports.add(report);
            }
            CommcareReport combinedFacilityReport = combineReports(providerReports);
            logger.warn("Combined report: "  + FormReporter.generateJsonString(combinedFacilityReport));
            combinedFacilityReports.add(combinedFacilityReport);
        }
        return combineReports(combinedFacilityReports);
    }

    private CommcareReport combineReports(List<CommcareReport> providerReports) {
        List<String> fieldsToTally = getFields();

        CommcareReport facilityReport = new CommcareReport();
        Map<String, String> facilityValues = facilityReport.getReportingValues();

        for (CommcareReport report : providerReports) {
            int numberOfForms = report.getNumberOfForms();
            facilityReport.setNumberOfForms(facilityReport.getNumberOfForms() + numberOfForms);
            Map<String, String> values = report.getReportingValues();
            for (String field : fieldsToTally) {
                try {
                    if (facilityValues.get(field) == null) {
                        facilityValues.put(field, "0");
                    }
                    facilityValues.put(field, (Integer.parseInt(facilityValues.get(field)) + Integer.parseInt(values.get(field))) + "");
                } catch (NumberFormatException e) {
                }
            }
        }

        return facilityReport;
    }

    private CommcareReport tallyForms(List<String> xformIds, DateTime date1, DateTime date2, Location facility) {
        List<CommcareForm> formsToCount = new ArrayList<CommcareForm>();
        for (String formId : xformIds) {
            CommcareForm form = formService.retrieveForm(formId);
            if (form != null) {
                String xmlns = form.getForm().getAttributes().get("xmlns");
                if ("http://openrosa.org/formdesigner/EA962226-BAB0-4F00-8BA3-FCCD821E7E97".equals(xmlns)) {
                    if (checkDates(form, date1, date2)) {
                        formsToCount.add(form);
                    }
                } 
            } 
        }
        CommcareReport report = FormReporter.calculateFields(getFields(), formsToCount);
        report.setLocationId(facility.getMotechId());
        report.setLocationName(facility.getCustomIdentifiers().get(0).getIdentifyingProperties().get(FACILITY_NAME));
        report.setStartDate(date1);
        report.setEndDate(date2);
        logger.warn(FormReporter.generateJsonString(report));
        return report;
    }



    private List<String> getFields() {
        List<String> fields = new ArrayList<String>();
        fields.add("new_acceptors");
        fields.add("repeat_acceptors");
        fields.add("total_acceptors");
        fields.add("first_anc");
        fields.add("attended_by_hew");
        fields.add("live_births");
        fields.add("still_births");
        fields.add("attended_by_tba");
        fields.add("total_births");
        fields.add("child_deaths");
        fields.add("early_neonatal_deaths");
        fields.add("maternal_deaths");
        fields.add("total_deaths");
        fields.add("lt3yr_weighed");
        fields.add("moderate_malnutrition");
        fields.add("severe_malnutrition");
        fields.add("lt1yr_given_measles_vaccine");
        fields.add("lt1yr_given_penta3_vaccine");
        fields.add("graduated_households");
        fields.add("condom_users");
        fields.add("oral_contraceptive_users");
        fields.add("injectable_users");
        fields.add("implanol_users");
        fields.add("malaria_lt5yr_male_p_falciparum_confirmed_new");
        fields.add("malaria_lt5yr_female_p_falciparum_confirmed_new");
        fields.add("malaria_lt5yr_male_other_confirmed_new");
        fields.add("malaria_lt5yr_female_other_confirmed_new");
        fields.add("tracer_drugs_out_of_stock");
        return fields;
    }

    private boolean checkDates(CommcareForm form, DateTime date1, DateTime date2) {
        FormValueElement formData = form.getForm();
        FormValueElement lastSubmitted = formData.getElementByName(CommcareConstants.LAST_SUBMITTED);

        if (lastSubmitted != null) {
            String lastSubmittedDate = lastSubmitted.getValue();
            if (lastSubmittedDate != null) {
                DateTime date = DateTime.parse(lastSubmittedDate);
                if (date.isAfter(date1) && date.isBefore(date2)) {
                    return true;
                }
            }
        }
        return false;

    }

    //    private void checkNumberOfForms() {
    //        List<Location> regions = locationService.getAllLocationsByType("region");
    //
    //        for (Location region : regions) {
    //            //Get facilities for each region
    //            List<Location> facilities = locationService.getAllChildrenByType(region.getMotechId(), FACILITY);
    //            for (Location facility : facilities) {
    //                List<ProviderIdBroker> providers = providerService.getProvidersByLocationId(facility.getMotechId());
    //                for (ProviderIdBroker provider : providers) {
    //                    logger.warn("HEW for facility " + facility.getCustomIdentifiers().get(0).getIdentifyingProperties().get(FACILITY_NAME) + " : " + provider.getMrsProvider().getPerson().getPreferredName());
    //                    String caseId = provider.getIdentifiers().get(1).getIdentifyingProperties().get(CASE_ID);
    //                    CaseInfo caseInfo = caseService.getCaseByCaseId(caseId);
    //                    checkForms(caseInfo.getXformIds());
    //                }
    //            }
    //        }
    //    }

    //    private void checkForms(List<String> xformIds) {
    //        List<CommcareForm> formsToCount = new ArrayList<CommcareForm>();
    //        for (String formId : xformIds) {
    //            CommcareForm form = formService.retrieveForm(formId);
    //            if (form != null) {
    //                String xmlns = form.getForm().getAttributes().get("xmlns");
    //                if ("http://openrosa.org/formdesigner/EA962226-BAB0-4F00-8BA3-FCCD821E7E97".equals(xmlns)) {
    //                    formsToCount.add(form);
    //                } 
    //            } 
    //        }
    //                tallyResult(formsToCount);
    //    }

    //    private void outputLocations() {
    //        List<Location> regions = locationService.getAllLocationsByType("region");
    //
    //        for (Location region : regions) {
    //            //Get facilities for each region
    //            List<Location> facilities = locationService.getAllChildrenByType(region.getMotechId(), FACILITY);
    //            logger.warn("Region: " + region.getCustomIdentifiers().get(0).getIdentifyingProperties().get(REGION) + " has " + facilities.size() + " facilities");
    //            for (Location facility : facilities) {
    //                logger.warn("Facility: " + facility.getCustomIdentifiers().get(0).getIdentifyingProperties().get(FACILITY_NAME));
    //            }
    //        }
    //    }

    private void initiateIdentifierTypes() {
        LocationIdentifierType regionId = locationIdService.getIdentifierTypeByName(REGION);
        if (regionId == null) {
            logger.warn("Registering new id type");
            regionId = new LocationIdentifierType();
            regionId.setIdentifierName(REGION);
            locationIdService.addIdentifierType(regionId);
        } else {
            logger.warn("Id type already exists.");
        }
        LocationIdentifierType woredaId = locationIdService.getIdentifierTypeByName(WOREDA);
        if (woredaId == null) {
            logger.warn("Registering new id type");
            woredaId = new LocationIdentifierType();
            woredaId.setIdentifierName(WOREDA);
            locationIdService.addIdentifierType(woredaId);
        } else {
            logger.warn("Id type already exists.");
        }
        LocationIdentifierType facilityId = locationIdService.getIdentifierTypeByName(FACILITY_NAME);
        if (facilityId == null) {
            logger.warn("Registering new id type");
            facilityId = new LocationIdentifierType();
            facilityId.setIdentifierName(FACILITY_NAME);
            locationIdService.addIdentifierType(facilityId);
        } else {
            logger.warn("Id type already exists.");
        }
    }

    private void addOrUpdateLocation(CaseInfo hewCase) {
        String region = hewCase.getFieldValues().get(REGION);
        String woreda = hewCase.getFieldValues().get(WOREDA);
        String healthFacility = hewCase.getFieldValues().get(FACILITY_NAME);

        Location parentRegion = addOrUpdateRegionLocation(region);
        Location parentWoreda = addOrUpdateWoredaLocation(parentRegion, woreda);
        Location facility = addOrUpdateHealthFacilityLocation(parentWoreda, healthFacility);

        addOrUpdateHew(facility, hewCase);
    }

    private void addOrUpdateHew(Location facility, CaseInfo hewCase) {
        String preferredName = hewCase.getFieldValues().get(HEW_NAME);
//        String mobileNumber = hewCase.getFieldValues().get(HEW_MOBILE_NUMBER);
        String caseId = hewCase.getCaseId();

        List<ProviderIdBroker> providers = providerService.getProvidersByPropertyAndValue(HEW_NAME, preferredName);

        if (providers == null || providers.size() > 0) {
            logger.warn("Provider already exists");
            //no updates for the time being
            return;
        }

        logger.warn("Registering new provider...");

        ProviderIdBroker providerBroker = new ProviderIdBroker();
        String motechId = UUID.randomUUID().toString();
        providerBroker.setMotechId(motechId);

//        Person person = new CouchPerson();
//        person.setPersonId(motechId);
//
//
//        person.setPreferredName(preferredName);
//
//        Attribute couchAttribute = new CouchAttribute();
//        couchAttribute.setName(HEW_MOBILE_NUMBER);
//        couchAttribute.setValue(mobileNumber);
//        person.getAttributes().add(couchAttribute);

//        Provider provider = new CouchProvider(motechId, person);

//        providerBroker.setMrsProvider(provider);

        List<String> locationIdentities = new ArrayList<String>();
        locationIdentities.add(facility.getMotechId());

        providerBroker.setLocationIdentities(locationIdentities);

        List<CustomProviderIdentifier> identifiers = new ArrayList<CustomProviderIdentifier>();

        CustomProviderIdentifier identifier = new CustomProviderIdentifier();
        identifier.setIdentifierType(COMMCARE_HEW);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put(HEW_NAME, preferredName);

        identifier.setIdentifyingProperties(properties);

        identifiers.add(identifier);

        CustomProviderIdentifier caseIdentifier = new CustomProviderIdentifier();
        caseIdentifier.setIdentifierType(COMMCARE_CASE);

        Map<String, String> caseProperties = new HashMap<String, String>();
        caseProperties.put(CASE_ID, caseId);

        caseIdentifier.setIdentifyingProperties(caseProperties);
        identifiers.add(caseIdentifier);

        providerBroker.setIdentifiers(identifiers);

        providerService.saveProvider(providerBroker);

    }

    private Location addOrUpdateHealthFacilityLocation(Location parentWoreda, String facilityName) {
        List<Location> locations = locationService.getLocationsByPropertyValue(FACILITY_NAME, facilityName);

        if (locations == null || locations.size() == 0) {
            logger.warn("Registering new health facility");
            Location facilityLocation = new Location();
            facilityLocation.setMotechId(UUID.randomUUID().toString());
            facilityLocation.setLocationType(FACILITY);
            List<CustomLocationIdentifier> customIdentifiers = new ArrayList<CustomLocationIdentifier>();
            CustomLocationIdentifier facilityId = new CustomLocationIdentifier();
            facilityId.setIdentifierType(FACILITY_NAME);
            Map<String, String> idProperties = new HashMap<String, String>();
            idProperties.put(FACILITY_NAME, facilityName);
            facilityId.setIdentifyingProperties(idProperties);
            customIdentifiers.add(facilityId);
            facilityLocation.setCustomIdentifiers(customIdentifiers);
            try {
                locationService.addChildLocation(parentWoreda, facilityLocation);
            } catch (LocationValidationException e) {
                logger.warn("Unable to save health facility location due to: " + e.getMessage());
            }
            return facilityLocation;
        } else {
            logger.warn("Health facility already exists");
            return locations.get(0);
        }
    }

    private Location addOrUpdateWoredaLocation(Location parentRegion, String woreda) {
        List<Location> locations = locationService.getLocationsByPropertyValue(WOREDA, woreda);

        if (locations == null || locations.size() == 0) {
            logger.warn("Registering new Woreda");
            Location woredaLocation = new Location();
            woredaLocation.setMotechId(UUID.randomUUID().toString());
            woredaLocation.setLocationType(WOREDA);
            List<CustomLocationIdentifier> customIdentifiers = new ArrayList<CustomLocationIdentifier>();
            CustomLocationIdentifier woredaId = new CustomLocationIdentifier();
            woredaId.setIdentifierType(WOREDA);
            Map<String, String> idProperties = new HashMap<String, String>();
            idProperties.put(WOREDA, woreda);
            woredaId.setIdentifyingProperties(idProperties);
            customIdentifiers.add(woredaId);
            woredaLocation.setCustomIdentifiers(customIdentifiers);
            try {
                locationService.addChildLocation(parentRegion, woredaLocation);
            } catch (LocationValidationException e) {
                logger.warn("Unable to save Woreda location due to: " + e.getMessage());
            }
            return woredaLocation;
        } else {
            logger.warn("Woreda already exists");
            return locations.get(0);
        }
    }

    private Location addOrUpdateRegionLocation(String region) {
        List<Location> locations = locationService.getLocationsByPropertyValue(REGION, region);

        if (locations == null || locations.size() == 0) {
            logger.warn("Registering new region");
            Location regionLocation = new Location();
            regionLocation.setMotechId(UUID.randomUUID().toString());
            regionLocation.setLocationType(REGION);
            List<CustomLocationIdentifier> customIdentifiers = new ArrayList<CustomLocationIdentifier>();
            CustomLocationIdentifier regionId = new CustomLocationIdentifier();
            regionId.setIdentifierType(REGION);
            Map<String, String> idProperties = new HashMap<String, String>();
            idProperties.put(REGION, region);
            regionId.setIdentifyingProperties(idProperties);
            customIdentifiers.add(regionId);
            regionLocation.setCustomIdentifiers(customIdentifiers);
            try {
                locationService.addChildLocation(null, regionLocation);
            } catch (LocationValidationException e) {
                logger.warn("Unable to save region location due to: " + e.getMessage());
            }
            return regionLocation;
        } else {
            logger.warn("Region already exists");
            return locations.get(0);
        }

    }
}
