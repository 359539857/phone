/**
 * 
 */
package com.tandong.workhour.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * @author PeterTan
 * 
 */
public class PropertyConfigurer extends PropertyPlaceholderConfigurer {
	private static Map<String, Object> ctxPropertiesMap = new HashMap<String, Object>();

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		// load properties to ctxPropertiesMap
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String value = props.getProperty(keyStr);
			ctxPropertiesMap.put(keyStr, value);
		}
	}

	// static method for accessing context properties
	public static Object getContextProperty(String name) {
		return ctxPropertiesMap.get(name);
	}

	public static String getContextPropertyStr(String name) {
		return ctxPropertiesMap.get(name) == null ? "" : ctxPropertiesMap.get(
				name).toString().trim();
	}
	
	public static Integer getContextPropertyInteger(String name) throws Exception{
		String value =  ctxPropertiesMap.get(name) == null ? "" : ctxPropertiesMap.get(
				name).toString().trim();
		return Integer.parseInt(value);
	}

	public static void addContextPropertyStr(String name, Object value) {
		if (ctxPropertiesMap.containsKey(name)) {
			ctxPropertiesMap.remove(name);
			ctxPropertiesMap.put(name, value);
		} else {
			ctxPropertiesMap.put(name, value);
		}
	}

	public static void removeContextProperty(String name) {
		ctxPropertiesMap.remove(name);
	}

	public static void clear() {
		ctxPropertiesMap.clear();
	}
}
