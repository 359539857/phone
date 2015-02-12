/**
 * 
 */
package com.tandong.workhour.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.tandong.workhour.utils.Constants;
import com.tandong.workhour.utils.DateUtil;
import com.tandong.workhour.utils.JSONFormat;
import com.tandong.workhour.utils.JSONUtils;

/**
 * @author PeterTan
 * 
 */
public class DTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5053058907664200399L;

	public static final Integer SUCCESS_CODE = 200;
	public static final Integer FAIL_CODE = 700;
	public static final Integer ERROR_CODE = 500;

	private Map<Object, Object> dtoMap = new HashMap<Object, Object>();

	public DTO() {
	}

	public DTO(Map<Object, Object> map) {
		dtoMap.putAll(map);
	}

	public DTO addAll(Map<Object, Object> map) {
		dtoMap.putAll(map);
		return this;
	}

	public static DTO parseJSONStringToDTO(String jsonstring)
			throws JSONException {
		if (jsonstring == null || jsonstring.isEmpty()) {
			throw new RuntimeException("jsonstring is null or length is 0");
		}
		DTO dto = new DTO(JSONUtils.parseJSONOStringToMap(jsonstring));
		return dto;
	}

	@SuppressWarnings("unchecked")
	public static DTO parseRequestParameterMapToDTO(Map<?, ?> parameterMap) {
		if (parameterMap == null || parameterMap.isEmpty()) {
			throw new RuntimeException("parameterMap is null or size is 0");
		}

		DTO dto = new DTO();

		Set<?> set = parameterMap.entrySet();
		Iterator<?> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<Object, Object> entry = (Entry<Object, Object>) iterator
					.next();
			Object key = entry.getKey();
			if (entry.getValue() instanceof String) {
				dto.addValue(key, entry.getValue().toString());
			} else if (entry.getValue() instanceof String[]) {
				String[] values = (String[]) entry.getValue();
				dto.addValue(key,
						values.length == 1 ? values[0] : Arrays.asList(values));
			} else {
				dto.addValue(key, entry.getValue());
			}

		}

		return dto;
	}

	/***
	 * 增加数据传输对象字段(支持多种类型)
	 * 
	 * @param k
	 *            key
	 * @param v
	 *            value
	 */
	public DTO addValue(Object k, Object v) {

		if (k == null) {
			throw new RuntimeException("k is null");
		}
		dtoMap.put(k, v);
		return this;

	}

	/***
	 * 根据数据传输对象池，传KEY获取VALUE,并返回实际的类型数据
	 * 
	 * @param k
	 *            key
	 * @param t
	 *            数据类型
	 * @return value
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(Object k) {

		String key = null;

		if (k == null) {
			throw new RuntimeException("k is null");
		} else {
			key = k.toString();
		}
		Object v = dtoMap.get(k);
		try {
			if (v == null) {
				return null;
			} else if (v instanceof Integer || key.startsWith("i_")) {// 整数
				return (T) Integer.valueOf(v.toString());
			} else if (v instanceof Boolean || key.startsWith("b_")) {// 布尔 类型
				return (T) Boolean.valueOf(v.toString());
			} else if (v instanceof Date || key.startsWith("dt_")) {// 布尔 类型
				return (T) DateUtil.format((Date) v, "yyyy-MM-dd HH:dd:ss");
			} else if (v instanceof Float || key.startsWith("f_")) { // 浮点类型
				return (T) Float.valueOf(v.toString());
			} else if (v instanceof Double || key.startsWith("d_")) {// 多精度类型
				return (T) Double.valueOf(v.toString());
			} else if (v instanceof Byte) {// 字节类型
				return (T) Byte.valueOf(v.toString());
			} else if (v instanceof Short) {// 短整形
				return (T) Short.valueOf(v.toString());
			} else if (v instanceof String || key.startsWith("str_")) {// 字符串
				return (T) v;
			} else {
				return (T) v;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T getPointValue(String key) {
		int endIndex = key.indexOf(".");
		if (endIndex != -1) {
			T t = this.getValue(key.substring(0, endIndex));
			String newKey = key.substring(endIndex + 1, key.length());
			if (t != null && !newKey.trim().equals("") && t instanceof DTO) {
				return ((DTO) t).getPointValue(newKey);
			} else {
				throw new RuntimeException("key Parameter is not valid ");
			}
		} else {
			return this.getValue(key);
		}
	}

	public boolean isEmpty() {
		return this.dtoMap.isEmpty();
	}

	/***
	 * 合并DTO对象，将b DTO对象合并到 a DTO对象
	 * 
	 * @param dto
	 *            被合并进来的DTO对象
	 */
	public DTO mergeDTO(DTO dto) {
		if (dto == null || dto.dtoMap.isEmpty()) {
			return null;
		}
		dtoMap.putAll(dto.dtoMap);
		return this;
	}

	/**
	 * 将DTO类型转换成JSON字符串
	 * 
	 * @return
	 */
	public String toJSONString() {
		JSONObject object = new JSONObject(JSONUtils.dtoToMap(this));
		return object.toString();
	}

	/***
	 * 将DTO数据类型转换成字符串
	 */
	@Override
	public String toString() {
		return JSONFormat.formatJson(toJSONString());
	}

	/***
	 * 验证DTO是否包含key
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key) {
		return dtoMap.containsKey(key);
	}

	/***
	 * 验证DTO是否包含value值
	 * 
	 * @param value
	 * @return
	 */
	public boolean containsValue(Object value) {
		return dtoMap.containsValue(value);
	}

	/***
	 * 将DTO类型转换成MAP类型
	 * 
	 * @return map
	 */
	public Map<Object, Object> toMap() {
		return dtoMap;
	}

	public Map<Object, Object> toAllMap() {
		return JSONUtils.dtoToMap(this);
	}

	/***
	 * 获取DTO对象条目列表对象
	 * 
	 * @return
	 */
	public Set<Entry<Object, Object>> entrySet() {
		return this.dtoMap.entrySet();
	}

	/***
	 * 获取DTO对象所有的KEY对象列表
	 * 
	 * @return
	 */
	public Set<Object> keySet() {
		return this.dtoMap.keySet();
	}

	public DTO remove(Object key) {
		if (this.dtoMap.containsKey(key))
			this.dtoMap.remove(key);

		return this;
	}

	public DTO clear() {
		this.dtoMap.clear();
		return this;
	}

	public Integer size() {
		if (dtoMap != null)
			return this.dtoMap.size();
		return 0;
	}

	public void addCode(Object o) {
		this.addValue(Constants.CODE, o);
	}

	/**
	 * 获取DTO对象所有的值对象列表
	 * 
	 * @return
	 */
	public Collection<Object> values() {
		return this.dtoMap.values();
	}

	/**
	 * 验证DTO类型字段是否为空
	 * 
	 * @param keys
	 * @return
	 */
	public boolean validate(Object... keys) {
		List<String> errorList = new ArrayList<String>();
		if (keys != null) {
			for (Object key : keys) {
				if (dtoMap.containsKey(key)) {
					Object obj = getValue(key);
					if (obj == null || obj.toString().trim().equals("")) {
						errorList.add(key + " value is null or void");
					}
				} else {
					errorList.add(key + " is not exist");
				}
			}
		} else {
			errorList.add("keys is null.");
		}

		if (!errorList.isEmpty()) {
			addValue("error", errorList);
		} else {
			return true;
		}
		return false;
	}

	/***
	 * 将DTO对象转换成get请求条件字符串<br>
	 * 
	 * 例:a=b&c=d&e=f
	 * 
	 * @return
	 */
	public String toHttpGetConditionString(Object... exceptKeys) {

		if (this.dtoMap == null)
			return null;

		StringBuffer condition = new StringBuffer();
		Set<Entry<Object, Object>> entryset = dtoMap.entrySet();
		Iterator<Entry<Object, Object>> iterator = entryset.iterator();
		if (exceptKeys == null) {
			while (iterator.hasNext()) {
				Entry<Object, Object> entry = iterator.next();
				condition.append(entry.getKey()).append("=")
						.append(entry.getValue()).append("&");
			}
		} else {
			List<Object> exceptKeyList = Arrays.asList(exceptKeys);
			for (Object key : exceptKeyList) {
				if (key.toString().indexOf(".") != -1) {
					condition
							.append("&")
							.append(key.toString().substring(
									key.toString().lastIndexOf(".") + 1))
							.append("=").append(getPointValue(key.toString()));
				} else {
					if (dtoMap.containsKey(key)) {
						condition.append("&").append(key).append("=")
								.append(dtoMap.get(key));
					}
				}
			}
		}

		return condition.toString();
	}

	/***
	 * 将DTO对象转换成get请求条件字符串<br>
	 * 
	 * 例:a=b&c=d&e=f
	 * 
	 * @return
	 */
	public String toHttpGetConditionString2(Object... exceptKeys) {

		if (this.dtoMap == null)
			return null;

		StringBuffer condition = new StringBuffer();
		Set<Entry<Object, Object>> entryset = dtoMap.entrySet();
		Iterator<Entry<Object, Object>> iterator = entryset.iterator();
		if (exceptKeys == null) {
			while (iterator.hasNext()) {
				Entry<Object, Object> entry = iterator.next();
				condition.append(entry.getKey()).append("=")
						.append(entry.getValue()).append("&");
			}
		} else {
			List<Object> exceptKeyList = Arrays.asList(exceptKeys);
			for (Object key : exceptKeyList) {
				if (key.toString().indexOf(".") != -1) {
					condition
							.append("&")
							.append(key.toString().substring(
									key.toString().lastIndexOf(".") + 1))
							.append("=").append(getPointValue(key.toString()));
				} else {
					if (dtoMap.containsKey(key)) {
						condition.append("&").append(key).append("=")
								.append(dtoMap.get(key));
					}
				}
			}
		}

		if (!condition.toString().isEmpty()) {
			condition.deleteCharAt(0);
		}

		return condition.toString();
	}

	public String getDate(String key, String f) {
		String value =  DateUtil.format(this.getValue(key).toString(), f);
		return value;
	}
}
