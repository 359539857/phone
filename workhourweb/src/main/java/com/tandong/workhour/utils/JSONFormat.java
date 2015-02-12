/**
 * 
 */
package com.tandong.workhour.utils;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author PeterTan
 * 
 */
public class JSONFormat {

	public static String formatJson(Map<Object, Object> jsonMap) {
		return JSONFormat.formatJson(new JSONObject(jsonMap).toString(), "    ");
	}

	public static String formatJson(String json) {
		return JSONFormat.formatJson(json, "    ");
	}

	/**
	 * json字符串的格式化
	 * 
	 * @author peiyuxin
	 * @param json
	 *            需要格式的json串
	 * @param fillStringUnit每一层之前的占位符号比如空格
	 *            制表符
	 * @return
	 */
	public static String formatJson(String json, String fillStringUnit) {
		if (json == null || json.trim().length() == 0) {
			return null;
		}

		int fixedLenth = 0;
		ArrayList<String> tokenList = new ArrayList<String>();
		{
			String jsonTemp = json;
			// 预读取
			while (jsonTemp.length() > 0) {
				String token = getToken(jsonTemp);
				jsonTemp = jsonTemp.substring(token.length());
				token = token.trim();
				tokenList.add(token);
			}
		}

		for (int i = 0; i < tokenList.size(); i++) {
			String token = tokenList.get(i);
			int length = token.getBytes().length;
			if (length > fixedLenth && i < tokenList.size() - 1 && tokenList.get(i + 1).equals(":")) {
				fixedLenth = length;
			}
		}

		StringBuilder buf = new StringBuilder();
		int count = 0;
		for (int i = 0; i < tokenList.size(); i++) {

			String token = tokenList.get(i);

			if (token.equals(",")) {
				buf.append(token);
				doFill(buf, count, fillStringUnit);
				continue;
			}
			if (token.equals(":")) {
				buf.append(" ").append(token).append(" ");
				continue;
			}
			if (token.equals("{")) {
				String nextToken = tokenList.get(i + 1);
				if (nextToken.equals("}")) {
					i++;
					buf.append("{ }");
				} else {
					count++;
					buf.append(token);
					doFill(buf, count, fillStringUnit);
				}
				continue;
			}
			if (token.equals("}")) {
				count--;
				doFill(buf, count, fillStringUnit);
				buf.append(token);
				continue;
			}
			if (token.equals("[")) {
				String nextToken = tokenList.get(i + 1);
				if (nextToken.equals("]")) {
					i++;
					buf.append("[ ]");
				} else {
					count++;
					buf.append(token);
					doFill(buf, count, fillStringUnit);
				}
				continue;
			}
			if (token.equals("]")) {
				count--;
				doFill(buf, count, fillStringUnit);
				buf.append(token);
				continue;
			}

			buf.append(token);
			// 左对齐
//			if (i < tokenList.size() - 1 && tokenList.get(i + 1).equals(":")) {
//				int fillLength = fixedLenth - token.getBytes().length;
//				if (fillLength > 0) {
//					for (int j = 0; j < fillLength; j++) {
//						buf.append(" ");
//					}
//				}
//			}
		}
		return buf.toString();
	}

	private static String getToken(String json) {
		StringBuilder buf = new StringBuilder();
		boolean isInYinHao = false;
		while (json.length() > 0) {
			String token = json.substring(0, 1);
			json = json.substring(1);

			if (!isInYinHao
			        && (token.equals(":") || token.equals("{") || token.equals("}") || token.equals("[") || token.equals("]") || token.equals(","))) {
				if (buf.toString().trim().length() == 0) {
					buf.append(token);
				}

				break;
			}

			if (token.equals("\\")) {
				buf.append(token);
				buf.append(json.substring(0, 1));
				json = json.substring(1);
				continue;
			}
			if (token.equals("\"")) {
				buf.append(token);
				if (isInYinHao) {
					break;
				} else {
					isInYinHao = true;
					continue;
				}
			}
			buf.append(token);
		}
		return buf.toString();
	}

	private static void doFill(StringBuilder buf, int count, String fillStringUnit) {
		buf.append("\n");
		for (int i = 0; i < count; i++) {
			buf.append(fillStringUnit);
		}
	}

	public static void main(String[] args) {
		String jsonString = "{\"responseCode\":0,\"timeStamp\":\"1369742449224\",\"digest\":\"BCBB889270D3DF0F8DE485E8A3FFD6A2\",\"rangeType\":1,\"silentDownload\":1,\"downloadDelay\":434,\"rom\":[{\"timeoutSeconds\":6456,\"preserveData\":654,\"dataUrl\":\"http://10.27.122.249:8080/fota-interface/rom/test.zip\",\"fullUpdate\":1,\"promptMessage\":\"GFDG\",\"promptMinutes\":53,\"versionCode\":65666,\"promptSize\":\"GFDG\",\"promptVersion\":\"FSDFSDF\",\"forceUpdate\":1,\"promptFeature\":\"GDFG\"}],\"app\":[],\"setting\":{\"uploadCrash\":null,\"updateStats\":0,\"uploadEvent\":0,\"checkinInterval\":0},\"deviceSettings\":{}}";
		System.out.println(JSONFormat.formatJson(jsonString, "    "));
	}

}
