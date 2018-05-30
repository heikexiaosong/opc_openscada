package com.gavel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static final String FILENAME = "config.properties";

    private static final Properties properties = new Properties();


    static {
        InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(FILENAME);
        if ( inputStream==null ){
            System.out.println("没有发现配置文件: config.properties");
        } else {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getValue(String key, String defaultValue){
        if ( !properties.containsKey(key) ){
            return defaultValue;
        }
        String value =  properties.getProperty(key);
        if ( value==null || value.length() == 0 ){
            value = defaultValue;
        }
        return value;
    }
}
