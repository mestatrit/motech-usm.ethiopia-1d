package org.motech.provider.repository.dao;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.motech.provider.repository.domain.MotechIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentifierDeserializer extends JsonDeserializer<MotechIdentifier> {
    
    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @Override
    public MotechIdentifier deserialize(JsonParser jsonParser, DeserializationContext arg1)
            throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        MotechIdentifier identifier = new MotechIdentifier();
        identifier.setExternalId(node.get("externalId").getTextValue());
        logger.warn("Id was: " + identifier.getExternalId());
        return identifier;
    }

}
