package org.motech.location.repository.dao;

import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.motech.location.repository.domain.MotechLocationIdentifier;

public class IdentifierDeserializer extends JsonDeserializer<MotechLocationIdentifier> {

    @Override
    public MotechLocationIdentifier deserialize(JsonParser jsonParser, DeserializationContext arg1)
            throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        MotechLocationIdentifier identifier = new MotechLocationIdentifier();
        identifier.setExternalId(node.get("externalId").getTextValue());
        return identifier;
    }

}
