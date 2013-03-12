package org.gates.ethiopia.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.motech.provider.repository.domain.CustomProviderIdentifier;
import org.motech.provider.repository.domain.ProviderIdBroker;
import org.motech.provider.repository.service.ProviderRepositoryService;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.commons.api.MotechException;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.Provider;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.services.ProviderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommcareToOpenMrsInitializer {

    @Autowired
    private CommcareUserService userService;

    @Autowired
    private ProviderRepositoryService providerService;

    @Autowired
    private ProviderAdapter providerAdapter;

    @PostConstruct
    public void testProviders() {
        List<OpenMRSProvider> providers = (List<OpenMRSProvider>) providerAdapter.getProviderByProviderId("198");
        if (providers.size() != 4) {
            throw new MotechException("There weren't 4");
        }
    }

//    @PostConstruct
    public void initializeProviders() {
        List<CommcareUser> commcareUsers = userService.getAllUsers();

        if (commcareUsers != null) {
            for (CommcareUser commcareUser : commcareUsers) {
                List<ProviderIdBroker> providers = providerService.getProvidersByPropertyAndValue("commcareId", commcareUser.getId());
                if (providers != null && providers.size() == 0) {
                    saveNewCommcareAndOpenMRSProvider(commcareUser);
                } 
            }
        }

    }


    private void saveNewCommcareAndOpenMRSProvider(CommcareUser commcareUser) {

        String generatedMotechId = UUID.randomUUID().toString();

        String firstName = commcareUser.getFirstName();
        String lastName = commcareUser.getLastName();

        Provider addOpenMrsProvider = providerAdapter.getProviderByProviderId(generatedMotechId).get(0);

        if (addOpenMrsProvider == null) {
            Provider newOpenMrsProvider = new OpenMRSProvider();
            Person openMrsPerson = new OpenMRSPerson();
            openMrsPerson.setFirstName(firstName);
            openMrsPerson.setLastName(lastName);
            newOpenMrsProvider.setProviderId(generatedMotechId);
            newOpenMrsProvider.setPerson(openMrsPerson);
            addOpenMrsProvider = providerAdapter.saveProvider(newOpenMrsProvider);
        }

        ProviderIdBroker provider = new ProviderIdBroker();

        List<CustomProviderIdentifier> customIdentifiers = new ArrayList<CustomProviderIdentifier>();

        CustomProviderIdentifier motechIdentifier = new CustomProviderIdentifier();
        motechIdentifier.setIdentifierType("motech");
        Map<String, String> motechIdProperties = new HashMap<String, String>();
        motechIdProperties.put("motechId", generatedMotechId);
        motechIdentifier.setIdentifyingProperties(motechIdProperties);

        CustomProviderIdentifier commcareIdentifier = new CustomProviderIdentifier();
        commcareIdentifier.setIdentifierType("commcare");
        Map<String, String> commcareIdProperties = new HashMap<String, String>();        
        commcareIdProperties.put("commcareId", commcareUser.getId());
        commcareIdentifier.setIdentifyingProperties(commcareIdProperties);

        CustomProviderIdentifier openMRSIdentifier = new CustomProviderIdentifier();
        openMRSIdentifier.setIdentifierType("openmrs");
        Map<String, String> openMrsIdProperties = new HashMap<String, String>();        
        openMrsIdProperties.put("openMrsId", addOpenMrsProvider.getPerson().getPersonId());
        openMRSIdentifier.setIdentifyingProperties(openMrsIdProperties);

        customIdentifiers.add(motechIdentifier);
        customIdentifiers.add(commcareIdentifier);
        customIdentifiers.add(openMRSIdentifier);
        
        provider.setMotechId(generatedMotechId);
        provider.setIdentifiers(customIdentifiers);

        
        providerService.saveProvider(provider);
    }


}
