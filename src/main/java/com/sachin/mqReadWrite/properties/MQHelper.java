package com.sachin.mqReadWrite.properties;

import ch.qos.logback.core.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

public class MQHelper {
    private static final String TMP_FOLDER = "/tmp/";

    private static void createTempFiles(String fileName) {
        Resource resource = new ClassPathResource(fileName);

        try (InputStream inputStream = resource.getInputStream()) {
            FileUtils.copyInputStreamToFile(inputStream, new File(FilenameUtils.normalize(TMP_FOLDER + fileName)));
        } catch (Exception e) {
            System.out.println("error" + e);
        }
    }

    public static void setProperties(String queueJKS) {
        createTempFiles(queueJKS);

        System.setProperty("javax.net.ssl.keyStore", TMP_FOLDER + queueJKS);
        System.setProperty("javax.net.ssl.trustStore", TMP_FOLDER + queueJKS);
        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
        System.setProperty("javax.net.debug", "true");


    }


}
