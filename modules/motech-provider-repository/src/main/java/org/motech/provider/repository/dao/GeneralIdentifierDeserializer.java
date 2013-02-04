package org.motech.provider.repository.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.motech.provider.repository.domain.CommcareProviderIdentifier;
import org.motech.provider.repository.domain.OpenMRSProviderIdentifier;
import org.motech.provider.repository.domain.ProviderIdentifier;

public class GeneralIdentifierDeserializer extends JsonDeserializer<List<ProviderIdentifier>> {

    //private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @Override
    public List<ProviderIdentifier> deserialize(JsonParser jsonParser, DeserializationContext arg1)
            throws IOException, JsonProcessingException {
        List<ProviderIdentifier> identifiers = new ArrayList<ProviderIdentifier>();
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        Iterator<JsonNode> nodes = node.getElements();

        while (nodes.hasNext()) {
            JsonNode localnode = nodes.next();

            String identifierType = localnode.get("identifierName").getTextValue();

            if ("openmrs_provider_id".equals(identifierType)) {
                identifiers.add(deserializeOpenMRSIdentifier(localnode));
            } else if ("commcare_provider_id".equals(identifierType)) {
                identifiers.add(deserializeCommcareIdentifier(localnode));
            }
        }
        return identifiers;   
    }


    private OpenMRSProviderIdentifier deserializeOpenMRSIdentifier(JsonNode node) {
        OpenMRSProviderIdentifier identifier = new OpenMRSProviderIdentifier();
        identifier.setUserName(node.get("userName").getTextValue());
        identifier.setUuid(node.get("uuid").getTextValue());
        return identifier;
    }

    private CommcareProviderIdentifier deserializeCommcareIdentifier(JsonNode node) {
        CommcareProviderIdentifier identifier = new CommcareProviderIdentifier();
        identifier.setDomain(node.get("domain").getTextValue());
        identifier.setUserId(node.get("userId").getTextValue());
        identifier.setUsername(node.get("username").getTextValue());
        return identifier;
    }

}
