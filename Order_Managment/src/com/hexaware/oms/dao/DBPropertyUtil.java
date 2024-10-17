package com.hexaware.oms.dao;

import java.util.Properties;
import java.io.InputStream;

public class DBPropertyUtil {

	public static String getDBConnectionString(String propertyFileName) throws Exception {
        Properties props = new Properties();
        InputStream input = DBPropertyUtil.class.getClassLoader().getResourceAsStream(propertyFileName);
        props.load(input);
        return props.getProperty("db.url");
    }
}
