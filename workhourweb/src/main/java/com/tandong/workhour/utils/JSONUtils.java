package com.tandong.workhour.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tandong.workhour.entity.DTO;

public class JSONUtils {

	@SuppressWarnings("rawtypes")
	public static Map<Object,Object> dtoToMap(DTO dto) {
		Map<Object,Object> map = new HashMap<Object,Object>();
		Set<Entry<Object, Object>> entrySet = dto.toMap().entrySet();
		Iterator<Entry<Object, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<Object, Object> entry = iterator.next();
			if (entry.getValue() instanceof DTO) {
				map.put(entry.getKey().toString(), dtoToMap((DTO) entry.getValue()));
			} else if (entry.getValue() instanceof List) {
				List list = (List) entry.getValue();
				List<Object> $list = new ArrayList<Object>();
				for (Object o : list) {
					if (o instanceof DTO) {
						DTO $dto = (DTO)o;
						$list.add(dtoToMap($dto));
					} else {
						$list.add(o);
					}
				}
				map.put(entry.getKey().toString(), $list);
			} else {
				map.put(entry.getKey().toString(), entry.getValue());
			}
		}
		return map;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<Object, Object> parseJSONOStringToMap(String jsonString) throws JSONException {
		Map<Object, Object> resultMap = new HashMap<Object, Object>();
		JSONObject jsonobject = new JSONObject(jsonString);
		Iterator<?> iterator = jsonobject.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object value = jsonobject.get(key);
			if (value instanceof JSONObject) {
				JSONObject newJsonobject = (JSONObject) value;
				resultMap.put(key, parseJSONOStringToMap(newJsonobject.toString()));
			} else if (value instanceof JSONArray) {
				List resultListMap = new ArrayList();
				JSONArray newJsonarray = (JSONArray) value;
				for (int i = 0; i < newJsonarray.length(); i++) {
					Object value2 = newJsonarray.get(i);
					if (value2 instanceof JSONObject) {
						JSONObject newJsonobject = (JSONObject) value2;
						resultListMap.add(parseJSONOStringToMap(newJsonobject.toString()));
					} else {
						resultListMap.add(value2);
					}
				}
				resultMap.put(key, resultListMap);
			} else {
				resultMap.put(key, value);
			}
		}
		return resultMap;
	}

	public static List<Map<Object,Object>> dtoListToMapList(List<DTO> dtoList) {
		if(dtoList != null && !dtoList.isEmpty()){
			List<Map<Object,Object>> mapList = new ArrayList<Map<Object,Object>>();
			for(DTO dto : dtoList){
				Map<Object,Object> map = JSONUtils.dtoToMap(dto);
				mapList.add(map);
			}
			return mapList;
		}
		return null;
	}
	
	public static void main(String[] args) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("fff", "fsdfds");
		obj.put("f3ff", "fsdfds");
		obj.put("ffrf", "fsdfds");
		obj.put("ssss", "fsdfds");
		JSONArray array = new JSONArray();
		array.put("fdfdf2");
		array.put("fdfdf3");
		array.put("fdfdf4");
		array.put("fdfdf5");
		obj.put("array1", array);
		JSONArray array2 = new JSONArray();
		JSONObject obj2 = new JSONObject();
		obj2.put("ffffff", "fsdfds");
		obj2.put("gggggg", "fsdfds");
		array2.put(obj2);

		JSONObject obj3 = new JSONObject();
		obj3.put("ffffff6", "fsdfds");
		obj3.put("gggggg7", "fsdfds");
		array2.put(obj3);
		obj.put("array2", array2);
		System.out.println(JSONUtils.parseJSONOStringToMap(obj.toString()));
	}
}
