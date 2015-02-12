/**
 * 
 */
package com.tandong.workhour.utils;

import java.util.HashMap;

/**
 * @author PeterTan
 * 
 */
public class DataMap extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5633391565848835150L;

	public DataMap() {
	};

	public DataMap(String key, String value) {
		this.put(key, value);
	}

	public DataMap add(String key, String value) {
		this.put(key, value);
		return this;
	}
}
