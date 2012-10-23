package org.motechproject.eventlogging.repository;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.eventlogging.domain.MappingsJson;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

@Component
public class AllEventMappings {

    public static final String MAPPING_FILE_NAME = "eventmappings.json";

    private MotechJsonReader motechJsonReader;

    private SettingsFacade settings;

    @Autowired
    public AllEventMappings(@Qualifier("eventLoggingSettings") SettingsFacade settings) {
        this.settings = settings;
        this.motechJsonReader = new MotechJsonReader();
    }

    public List<MappingsJson> getAllMappings() {
        Type type = new TypeToken<List<MappingsJson>>() {
        }.getType();

        InputStream is = settings.getRawConfig(MAPPING_FILE_NAME);
        
        Logger logger = LoggerFactory.getLogger("gates-ethiopia");

        List<MappingsJson> mappings = (List<MappingsJson>) motechJsonReader.readFromStream(is, type);
        
        logger.warn(" " + mappings.size());

        return mappings;
    }
}
