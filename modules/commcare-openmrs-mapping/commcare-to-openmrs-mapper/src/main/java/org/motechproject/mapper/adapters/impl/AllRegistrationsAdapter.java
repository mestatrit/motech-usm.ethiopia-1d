package org.motechproject.mapper.adapters.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.adapters.mappings.MRSActivity;
import org.motechproject.mapper.adapters.mappings.OpenMRSRegistrationActivity;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.OpenMRSCommcareUtil;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.OpenMRSAttribute;
import org.motechproject.mrs.model.OpenMRSFacility;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.services.PatientAdapter;
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
    private PatientAdapter mrsPatientAdapter;

    @Autowired
    private CommcareUserService userService;

    @Override
    public void adaptForm(CommcareForm form, MRSActivity activity) {

        OpenMRSRegistrationActivity registrationActivity = (OpenMRSRegistrationActivity) activity;

        FormValueElement topFormElement = form.getForm();

        Map<String, String> idScheme = registrationActivity.getIdScheme();
        Map<String, String> mappedAttributes = registrationActivity.getAttributes();
        Map<String, String> registrationMappings = registrationActivity.getRegistrationMappings();

        String idSchemeType = idScheme.get(FormMappingConstants.ID_SCHEME_TYPE);
        String idFieldName = idScheme.get(FormMappingConstants.ID_SCHEME_FIELD);

        String motechId = null;

        if (idSchemeType.equals(FormMappingConstants.DEFAULT_ID_SCHEME)) {
            // id field exists in form
            List<FormValueElement> subElements = (List<FormValueElement>) topFormElement.getSubElements().get(
                    idFieldName);
            motechId = subElements.get(0).getValue();
            logger.debug("MoTeCH Id retrieved: " + motechId);
        } else if (idSchemeType.equals(FormMappingConstants.COMMCARE_ID_SCHEME)) {
            logger.error("Still need to implement Commcare ID scheme");
        } else {
            logger.debug("No ID scheme was specified");
        }

        OpenMRSPatient patient = (OpenMRSPatient) mrsPatientAdapter.getPatientByMotechId(motechId);

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

        OpenMRSFacility facility = null;

        String facilityName = populateStringValue(facilityNameField, topFormElement);

        if (facilityName == null) {
            facilityName = registrationActivity.getStaticMappings().get("facility");
        }

        if (facilityName != null) {
            facility = (OpenMRSFacility) openMrsUtil.findFacility(facilityName);
        } else {
            facilityName = openMrsUtil.getFacility(form);

            //                if (registrationActivity.getFacilityScheme() != null && "commcareUser".equals(registrationActivity.getFacilityScheme().get("type"))) {
            //                    String facilityUserFieldName = registrationActivity.getFacilityScheme().get("fieldName");
            //                    if (facilityUserFieldName != null) {
            //                        String userId = form.getMetadata().get("userID");
            //                        logger.info("Retreiving user: " + userId);
            //                        CommcareUser user = userService.getCommcareUserById(userId);
            //                        if (user != null) {
            //                            logger.info("User: " + user.getId() + " is being used");
            //                            if (user.getUserData() != null) {
            //                                facilityName = user.getUserData().get(facilityUserFieldName);
            //                            }
            //                        } else {
            //                            logger.info("Could not find user: " + userId);
            //                        }
            //                    }
            //                } else {
            //                    logger.info("No facility scheme defined");
            //                }
        }

        if (facilityName == null) {
            logger.warn("No facility name provided, using " + FormMappingConstants.DEFAULT_FACILITY);
            facilityName = FormMappingConstants.DEFAULT_FACILITY;
            facility = (OpenMRSFacility) openMrsUtil.findFacility(facilityName);
        } else {
            facility = (OpenMRSFacility) openMrsUtil.findFacility(facilityName);
        }

        OpenMRSPerson person = null;

        if (patient == null && facility != null && firstName != null && lastName != null && dateOfBirth != null
                && motechId != null) {
            person = new OpenMRSPerson().firstName(firstName).lastName(lastName).gender(gender).dateOfBirth(new DateTime(dateOfBirth));
            if (mappedAttributes != null) {
                for (Entry<String, String> entry : mappedAttributes.entrySet()) {
                    FormValueElement attributeElement = topFormElement.getElementByName(entry.getValue());
                    String attributeValue = null;
                    if (attributeElement != null) {
                        attributeValue = attributeElement.getValue();
                    }
                    if (attributeValue != null && attributeValue.trim().length() > 0) {
                        String attributeName = entry.getKey();
                        OpenMRSAttribute attribute = new OpenMRSAttribute(attributeName, attributeValue);
                        person.addAttribute(attribute);
                    }
                }
            }

            if (middleName != null) {
                person.middleName(middleName);
            }

            if (preferredName != null) {
                person.preferredName(preferredName);
            }

            if (address != null) {
                person.address(address);
            }

            if (birthDateIsEstimated != null) {
                person.birthDateEstimated(birthDateIsEstimated);
            }

            if (age != null) {
                person.age(age);
            }

            if (isDead != null) {
                person.dead(isDead);
            }

            if (deathDate != null) {
                person.deathDate(new DateTime(deathDate));
            }

            patient = new OpenMRSPatient(motechId, person, facility);
            try {
                patient = (OpenMRSPatient) mrsPatientAdapter.savePatient(patient);
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

    private void updatePatient(OpenMRSPatient patient, OpenMRSPerson person, String firstName, String lastName,
            Date dateOfBirth, String gender, String middleName, String preferredName, String address,
            Boolean birthDateIsEstimated, Integer age, Boolean isDead, Date deathDate) {
        if (firstName != null) {
            person.firstName(firstName);
        }
        if (lastName != null) {
            person.lastName(lastName);
        }
        if (dateOfBirth != null) {
            person.dateOfBirth(new DateTime(dateOfBirth));
        }
        if (gender != null) {
            person.gender(gender);
        }

        if (middleName != null) {
            person.middleName(middleName);
        }

        if (preferredName != null) {
            person.preferredName(preferredName);
        }

        if (address != null) {
            person.address(address);
        }

        if (birthDateIsEstimated != null) {
            person.birthDateEstimated(birthDateIsEstimated);
        }

        if (age != null) {
            person.age(age);
        }

        if (isDead != null) {
            person.dead(isDead);
        }

        if (deathDate != null) {
            person.deathDate(new DateTime(deathDate));
        }

        mrsPatientAdapter.updatePatient(patient);
    }

    private Integer populateIntegerValue(String fieldName, FormValueElement topFormElement) {
        if (fieldName != null) {
            FormValueElement element = topFormElement.getElementByName(fieldName);
            if (element != null) {
                Integer value = null;
                try {
                    value = new Integer(element.getValue());
                } catch (NumberFormatException e) {
                    logger.error("Error parsing age value from registration form: " + e.getMessage());
                    return null;
                }
            }
        }
        return null;
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
