package org.motech.location.repository.dao;

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
import org.motech.location.repository.domain.CommcareLocationIdentifier;
import org.motech.location.repository.domain.LocationIdentifier;
import org.motech.location.repository.domain.OpenMRSLocationIdentifier;

public class LocationIdentifierDeserializer extends JsonDeserializer<List<LocationIdentifier>> {

    @Override
    public List<LocationIdentifier> deserialize(JsonParser jsonParser, DeserializationContext arg1)
            throws IOException, JsonProcessingException {
        List<LocationIdentifier> identifiers = new ArrayList<LocationIdentifier>();
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        Iterator<JsonNode> nodes = node.getElements();
        while (nodes.hasNext()) {
            JsonNode localnode = nodes.next();

            String identifierType = localnode.get("identifierName").getTextValue();

            if ("openmrs_location_id".equals(identifierType)) {
                identifiers.add(deserializeOpenMRSIdentifier(localnode));
            } else if ("commcare_location_id".equals(identifierType)) {
                identifiers.add(deserializeCommcareIdentifier(localnode));
            }
        }
        return identifiers;   
    }


    private OpenMRSLocationIdentifier deserializeOpenMRSIdentifier(JsonNode node) {
        OpenMRSLocationIdentifier identifier = new OpenMRSLocationIdentifier();
        identifier.setFacilityName(node.get("facilityName").getTextValue());
        identifier.setUuid(node.get("uuid").getTextValue());
        return identifier;
    }

    private CommcareLocationIdentifier deserializeCommcareIdentifier(JsonNode node) {
        CommcareLocationIdentifier identifier = new CommcareLocationIdentifier();
        identifier.setDomain(node.get("domain").getTextValue());
        identifier.setLocationFieldValue(node.get("locationFieldValue").getTextValue());
        identifier.setLocationid(node.get("locationid").getTextValue());
        return identifier;
    }

}
