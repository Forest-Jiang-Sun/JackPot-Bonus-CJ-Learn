package com.aspectgaming.common.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ligang.yao
 */
public final class XMLUtil {

    private static final Logger log = LoggerFactory.getLogger(XMLUtil.class);

    public static <T> T unmarshal(final String file, final Class<T> clazz) {
        File path = new File(file);
        log.info("Loading {} from {}", clazz.getSimpleName(), path.getAbsolutePath());
        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
            unmarshaller.setSchema(null); // no validation
            return clazz.cast(unmarshaller.unmarshal(path));
        } catch (JAXBException je) {
            throw new RuntimeException("Error loading", je);
        }
    }

    private XMLUtil() {}
}
