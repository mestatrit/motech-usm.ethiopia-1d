package org.motechproject.mapper.adapters.impl;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.adapters.mappings.MRSActivity;
import org.motechproject.mapper.adapters.mappings.OpenMRSRegistrationActivity;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.OpenMRSCommcareUtil;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSFacilityDto;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.model.MRSPersonDto;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllRegistrationsAdapter implements ActivityFormAdapter {

    private Logger logger = LoggerFactory.getLogger("commcare-openmrs-mapper");

    @Autowired
    private OpenMRSCommcareUtil openMrsUtil;

    @Autowired
    private MRSFacilityAdapter facilityAdapter;

    public void unknownFacilityBootstrap() {
        MRSFacility facility = new MRSFacilityDto();
        facility.setFacilityId("facilityId");
        facility.setName("Unknown Location");
        facilityAdapter.saveFacility(facility);

        MRSFacility facility2 = new MRSFacilityDto();
        facility2.setFacilityId("facilityId2");
        facility2.setName("University of Southern Maine");
        facilityAdapter.saveFacility(facility2);
    }

    @Autowired
    private MRSPatientAdapter mrsPatientAdapter;

    @Autowired
    private CommcareUserService userService;

    /* CHECKSTYLE:OFF */
    @Override
    public void adaptForm(CommcareForm form, MRSActivity activity) {

        unknownFacilityBootstrap();

        OpenMRSRegistrationActivity registrationActivity = (OpenMRSRegistrationActivity) activity;

        FormValueElement topFormElement = form.getForm();

        Map<String, String> idScheme = registrationActivity.getIdScheme();
        //        Map<String, String> mappedAttributes = registrationActivity.getAttributes();
        Map<String, String> registrationMappings = registrationActivity.getRegistrationMappings();

        String idSchemeType = idScheme.get(FormMappingConstants.ID_SCHEME_TYPE);
        String idFieldName = idScheme.get(FormMappingConstants.ID_SCHEME_FIELD);

        String motechId = null;

        if (idSchemeType.equals(FormMappingConstants.DEFAULT_ID_SCHEME)) {
            // id field exists in form
            Set<FormValueElement> subElements = (Set<FormValueElement>) topFormElement.getSubElements().get(
                    idFieldName);
            motechId = subElements.iterator().next().getValue();
            logger.debug("MoTeCH Id retrieved: " + motechId);
        } else if (idSchemeType.equals(FormMappingConstants.COMMCARE_ID_SCHEME)) {
            logger.error("Still need to implement Commcare ID scheme");
        } else {
            logger.debug("No ID scheme was specified");
        }

        MRSPatient patient = mrsPatientAdapter.getPatientByMotechId(motechId);

        if (patient == null) {
            logger.info("Registering new patient by MotechId " + motechId);
        } else {
            logger.info("Patient already exists, updating patient " + motechId);
        }

        String dobField = registrationMappings.get(FormMappingConstants.DOB_FIELD);
        String firstNameField = registrationMappings.get(FormMappingConstants.FIRST_NAME_FIELD);
        String middleNameField = registrationMappings.get(FormMappingConstants.MIDDLE_NAME_FIELD);
        String lastNameField = registrationMappings.get(FormMappingConstants.LAST_NAME_FIELD);
        String preferredNameField = registrationMappings.get(FormMappingConstants.PREFERRED_NAME_FIELD);
        String genderField = registrationMappings.get(FormMappingConstants.GENDER_FIELD);
        String addressField = registrationMappings.get(FormMappingConstants.ADDRESS_FIELD);
        String ageField = registrationMappings.get(FormMappingConstants.AGE_FIELD);
        String birthDateIsEstimatedField = registrationMappings.get(FormMappingConstants.BIRTH_DATE_ESTIMATED_FIELD);
        String isDeadField = registrationMappings.get(FormMappingConstants.IS_DEAD_FIELD);
        String deathDateField = registrationMappings.get(FormMappingConstants.DEATH_DATE_FIELD);
        String facilityNameField = registrationMappings.get(FormMappingConstants.FACILITY_NAME_FIELD);

        String gender = populateStringValue(genderField, topFormElement);

        if (gender == null) {
            gender = registrationActivity.getStaticMappings().get("gender");
        }

        Date dateOfBirth = populateDateValue(dobField, topFormElement);

        if (dateOfBirth == null) {
            dateOfBirth = DateTime.parse(registrationActivity.getStaticMappings().get("dob")).toDate();
        }

        String firstName = populateStringValue(firstNameField, topFormElement);

        if (firstName == null) {
            firstName = registrationActivity.getStaticMappings().get("firstName");
        }

        String lastName = populateStringValue(lastNameField, topFormElement);

        if (lastName == null) {
            lastName = registrationActivity.getStaticMappings().get("lastName");
        }

        String middleName = populateStringValue(middleNameField, topFormElement);

        if (middleName == null) {
            middleName = registrationActivity.getStaticMappings().get("middleName");
        }

        String preferredName = populateStringValue(preferredNameField, topFormElement);

        if (preferredName == null) {
            preferredName = registrationActivity.getStaticMappings().get("preferredName");
        }

        String address = populateStringValue(addressField, topFormElement);

        if (address == null) {
            address = registrationActivity.getStaticMappings().get("address");
        }

        Integer age = populateIntegerValue(ageField, topFormElement);

        if (age == null) {
            try {
                age = Integer.parseInt(registrationActivity.getStaticMappings().get("age"));
            } catch (NumberFormatException e) {
                logger.error("Age was not a valid number");
            }
        }

        Boolean birthDateIsEstimated = populateBooleanValue(birthDateIsEstimatedField, topFormElement);

        if (birthDateIsEstimated == null) {
            try {
                birthDateIsEstimated = Boolean.parseBoolean(registrationActivity.getStaticMappings().get(
                        "birthdateIsEstimated"));
            } catch (Exception e) {
                logger.error("Error in birthdate: " + e.getMessage());
            }
        }

        Boolean isDead = populateBooleanValue(isDeadField, topFormElement);

        if (isDead == null) {
            try {
                isDead = Boolean.parseBoolean(registrationActivity.getStaticMappings().get("dead"));
            } catch (Exception e) {
                logger.error("Error in is dead value: " + e.getMessage());
            }
        }

        Date deathDate = populateDateValue(deathDateField, topFormElement);

        if (deathDate == null) {
            try {
                deathDate = DateTime.parse(registrationActivity.getStaticMappings().get("deathDate")).toDate();
            } catch (Exception e) {
                logger.error("Error in death date: " + e.getMessage());
            }
        }

        MRSFacility facility = null;

        String facilityName = populateStringValue(facilityNameField, topFormElement);

        if (facilityName == null) {
            facilityName = registrationActivity.getStaticMappings().get("facility");
        }

        if (facilityName != null) {
            facility = openMrsUtil.findFacility(facilityName);
        } else {

            if (registrationActivity.getFacilityScheme() != null &&
                    "commcareUser".equals(registrationActivity.getFacilityScheme().get("type")))
            {
                String facilityUserFieldName =
                        registrationActivity.getFacilityScheme().get("fieldName");
                if (facilityUserFieldName != null) {
                    String userId = form.getMetadata().get("userID");
                    logger.info("Retreiving user: " + userId);
                    CommcareUser user = userService.getCommcareUserById(userId);
                    if (user != null) {
                        logger.info("User: " + user.getId() + " is being used");
                        if (user.getUserData() != null) {
                            facilityName = user.getUserData().get(facilityUserFieldName);
                        }
                    } else {
                        logger.info("Could not find user: " + userId);
                    }
                }
            } else {
                logger.info("No facility scheme defined");
            }
        }

        if (facilityName == null) {
            logger.warn("No facility name provided, using " + FormMappingConstants.DEFAULT_FACILITY);
            facilityName = FormMappingConstants.DEFAULT_FACILITY;
            facility = openMrsUtil.findFacility(facilityName);
        } else {
            facility = openMrsUtil.findFacility(facilityName);
        }

        logger.info("Facility name: " + facilityName);

        MRSPerson person = null;

        if (patient == null && facility != null && firstName != null && lastName != null && dateOfBirth != null
                && motechId != null) {
            person = new MRSPersonDto();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setGender(gender);
            person.setDateOfBirth(new DateTime(dateOfBirth));

            //            if (mappedAttributes != null) {
            //                for (Entry<String, String> entry : mappedAttributes.entrySet()) {
            //                    FormValueElement attributeElement = topFormElement.getElementByName(entry.getValue());
            //                    String attributeValue = null;
            //                    if (attributeElement != null) {
            //                        attributeValue = attributeElement.getValue();
            //                    }
            //                    if (attributeValue != null && attributeValue.trim().length() > 0) {
            //                        String attributeName = entry.getKey();
            //                        OpenMRSAttribute attribute = new OpenMRSAttribute(attributeName, attributeValue);
            //                        person.addAttribute(attribute);
            //                    }
            //                }
            //            }

            setPerson(middleName, preferredName, address, birthDateIsEstimated, age, isDead, deathDate, person);

            patient = new MRSPatientDto();
            patient.setMotechId(motechId);
            patient.setPerson(person);

            try {
                patient = mrsPatientAdapter.savePatient(patient);
                logger.info("New patient saved: " + motechId);
            } catch (MRSException e) {
                logger.info("Could not save patient: " + e.getMessage());
            }
        } else if (patient != null) {
            person = patient.getPerson();
            updatePatient(patient, person, firstName, lastName, dateOfBirth, gender, middleName, preferredName,
                    address, birthDateIsEstimated, age, isDead, deathDate);
        } else {
            logger.info("Unable to save patient due to missing information");
            if (facility == null) {
                logger.info("Reason: No facility provided");
            }
            if (firstName == null) {
                logger.info("Reason: No first name provided");
            }
            if (lastName == null) {
                logger.info("Reason: No last name provided");
            }
            if (dateOfBirth == null) {
                logger.info("Reason: No date of birth provided");
            }
            if (motechId == null) {
                logger.info("Reason: No MOTECH id provided");
            }
        }
    }
    /* CHECKSTYLE:ON */

    /* CHECKSTYLE:OFF */
    private void setPerson(String middleName, String preferredName, String address, Boolean birthDateIsEstimated,
            Integer age, Boolean isDead, Date deathDate, MRSPerson person) {
        /* CHECKSTYLE:ON */
        if (middleName != null) {
            person.setMiddleName(middleName);
        }

        if (preferredName != null) {
            person.setPreferredName(preferredName);
        }

        if (address != null) {
            person.setAddress(address);
        }

        if (birthDateIsEstimated != null) {
            person.setBirthDateEstimated(birthDateIsEstimated);
        }

        if (age != null) {
            person.setAge(age);
        }

        if (isDead != null) {
            person.setDead(isDead);
        }

        if (deathDate != null) {
            person.setDeathDate(new DateTime(deathDate));
        }
    }

    /* CHECKSTYLE:OFF */
    private void updatePatient(MRSPatient patient, MRSPerson person, String firstName, String lastName,
            Date dateOfBirth, String gender, String middleName, String preferredName, String address,
            Boolean birthDateIsEstimated, Integer age, Boolean isDead, Date deathDate) {
        /* CHECKSTYLE:ON */
        if (firstName != null) {
            person.setFirstName(firstName);
        }
        if (lastName != null) {
            person.setLastName(lastName);
        }
        if (dateOfBirth != null) {
            person.setDateOfBirth(new DateTime(dateOfBirth));
        }
        if (gender != null) {
            person.setGender(gender);
        }

        setPerson(middleName, preferredName, address, birthDateIsEstimated, age, isDead, deathDate, person);


        mrsPatientAdapter.updatePatient(patient);
    }

    private Integer populateIntegerValue(String fieldName, FormValueElement topFormElement) {
        Integer value = null;
        if (fieldName != null) {
            FormValueElement element = topFormElement.getElementByName(fieldName);
            if (element != null) {
                try {
                    value = Integer.valueOf(element.getValue());
                } catch (NumberFormatException e) {
                    logger.error("Error parsing age value from registration form: " + e.getMessage());
                    return null;
                }
            }
        }
        return value;
    }

    private Boolean populateBooleanValue(String fieldName, FormValueElement topFormElement) {
        if (fieldName != null) {
            FormValueElement element = topFormElement.getElementByName(fieldName);
            if (element != null) {
                return new Boolean(element.getValue());
            }
        }
        return null;
    }

    private Date populateDateValue(String fieldName, FormValueElement topFormElement) {
        if (fieldName != null) {
            FormValueElement element = topFormElement.getElementByName(fieldName);
            if (element != null) {
                return DateTime.parse(element.getValue()).toDate();
            }
        }
        return null;
    }

    private String populateStringValue(String fieldName, FormValueElement topFormElement) {
        if (fieldName != null) {
            FormValueElement element = topFormElement.getElementByName(fieldName);
            if (element != null) {
                return element.getValue();
            }
        }
        return null;
    }
}
