package com.hexaware.oms.util;

import java.io.InputStream;
import java.util.Properties;


public class DBPropertyUtil {
	
	public static String getDBConnectionString(String propertyFileName) throws Exception {
        Properties props = new Properties();
        InputStream input = DBPropertyUtil.class.getClassLoader().getResourceAsStream(propertyFileName);
        props.load(input);
        return props.getProperty("db.url");
    }

}
