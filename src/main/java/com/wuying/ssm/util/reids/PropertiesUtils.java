package com.wuying.ssm.util.reids;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件读取工具类
 * 
 * @author baoxu
 *
 * @version 1.0
 * 
 */
public class PropertiesUtils {

    private static Logger logger = LoggerFactory
            .getLogger(PropertiesUtils.class);
    private static String application_path = "/application.properties";
    private static String parameter_path = "/system.properties";

    private static Properties props_application = new Properties();
    private static Properties props_parameter = new Properties();

    private static void loadProperties(Properties props, String propertiesPath) {
        try {
            props.load(PropertiesUtils.class
                    .getResourceAsStream(propertiesPath));
        } catch (IOException e) {
            logger.error("加载配置文件失败！path:" + propertiesPath, e);
        }
    }

    private static String readValue(Properties props, String propertiesPath,
            String key) {
        if (props == null || props.isEmpty()) {
            loadProperties(props, propertiesPath);
        }
        return props.getProperty(key);
    }

    public static synchronized String readApplicationValue(String key) {
        return readValue(props_application, application_path, key);
    }

    public static synchronized String readSystemParamValue(String key) {
        return readValue(props_parameter, parameter_path, key);
    }

    /**
     * 解析ini
     * 
     * @param file
     * @return
     */
    public static synchronized Map<String, HashMap<String, String>> loadData(
            File file) {
        Map<String, HashMap<String, String>> sectionsMap = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> itemsMap = new HashMap<String, String>();
        String currentSection = "";

        BufferedReader reader = null;
        try {
            // reader = new BufferedReader(new FileReader(file));
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if ("".equals(line))
                    continue;
                if (line.startsWith("[") && line.endsWith("]")) {
                    // Ends last section
                    if (itemsMap.size() > 0
                            && !"".equals(currentSection.trim())) {
                        sectionsMap.put(currentSection, itemsMap);
                    }
                    currentSection = "";
                    itemsMap = null;

                    // Start new section initial
                    currentSection = line.substring(1, line.length() - 1);
                    itemsMap = new HashMap<String, String>();
                } else {
                    int index = line.indexOf("=");
                    if (index != -1) {
                        String key = line.substring(0, index);
                        String value = line.substring(index + 1, line.length());
                        itemsMap.put(key, value);
                    }
                }
                // System.out.println(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sectionsMap;
    }
}
