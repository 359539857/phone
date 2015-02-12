/**
 * 
 */
package com.tandong.workhour.utils;

import java.util.Set;


/**
 * @author PeterTan
 * 
 */
public class UserCache {

	private static java.util.Hashtable<String, Object> cacheList = new java.util.Hashtable<String, Object>();

	public static <T> void  add(String key,T value){
		if(cacheList.containsKey(key)){
			cacheList.remove(key);
		}
		cacheList.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String key){
		if(cacheList.containsKey(key)){
			return (T) cacheList.get(key);
		}else{
			return null;
		}
	}
	
	public static Set<String> keySet(){
		 return  cacheList.keySet();
	}
	
	public void clear(){
		cacheList.clear();
	}
	
	

}
