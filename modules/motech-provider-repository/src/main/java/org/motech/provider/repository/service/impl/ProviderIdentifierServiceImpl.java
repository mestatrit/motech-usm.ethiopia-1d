package org.motech.provider.repository.service.impl;

import java.util.List;
import org.motech.provider.repository.dao.ProviderIdentifierCouchDAO;
import org.motech.provider.repository.domain.ProviderIdentifierType;
import org.motech.provider.repository.service.ProviderIdentifierService;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProviderIdentifierServiceImpl implements ProviderIdentifierService {

    private MotechJsonReader jsonReader = new MotechJsonReader();

    @Autowired
    private ProviderIdentifierCouchDAO providerIdentifierDAO;

    @Override
    public List<ProviderIdentifierType> getAllIdentifierTypes() {
        return providerIdentifierDAO.getAllIdentifierTypes();
    }

    @Override
    public ProviderIdentifierType getIdentifierTypeByName(String identifierName) {
        return providerIdentifierDAO.getIdentifierTypeByName(identifierName);
    }

    @Override
    public void addIdentifierType(ProviderIdentifierType identifierType) {
        providerIdentifierDAO.add(identifierType);
    }

    @Override
    public void addIdentifierTypeJson(String identifierTypeJson) {
        ProviderIdentifierType identifierType = (ProviderIdentifierType) jsonReader.readFromString(identifierTypeJson,  ProviderIdentifierType.class);
        addIdentifierType(identifierType);
    }

    @Override
    public void removeIdentifierType(String identifierName) {
        ProviderIdentifierType type = getIdentifierTypeByName(identifierName);
        if (type != null) {
            providerIdentifierDAO.remove(type);
        }
    }
}
