package com.tandong.workhour.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tandong.workhour.entity.DTO;

public class WorkHourHttpUtils {

	private static String loginURL = "http://redmine.china-liantong.com:8000/login";

	private static CloseableHttpClient client = HttpClients.createDefault();

	public static String get(String url) throws ClientProtocolException, IOException {
		System.out.println(url);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = client.execute(httpGet);
		HttpEntity entity = httpResponse.getEntity();
		if (entity != null) {
			return printResponse(httpResponse);
		}
		return null;
	}

	public static boolean validatorLogin(String html) {
		Document doc = Jsoup.parse(html);
		Element element = doc.getElementById("login-form");
		Element body = doc.getElementsByTag("body").get(0);
		if (element == null) {
			if (body.html().indexOf("http://redmine.china-liantong.com:8000/login?back_url") < 0) {
				return false;
			}
		}
		return true;
	}

	public static String getAuthenticityToken(String html) {
		Document doc = Jsoup.parse(html);
		Element element = doc.getElementById("login-form");
		Elements elements = element.getElementsByAttributeValue("name", "authenticity_token");
		Element element2 = elements.get(0);
		String authenticity_token = element2.attr("value");
		return authenticity_token;
	}
	
	public static String getAuthenticityToken2(String html,String id) {
		Document doc = Jsoup.parse(html);
		Element element = doc.getElementById(id);
		Elements elements = element.getElementsByAttributeValue("name", "authenticity_token");
		Element element2 = elements.get(0);
		String authenticity_token = element2.attr("value");
		return authenticity_token;
	}
	
	public static String getToken(String html) {
		Document doc = Jsoup.parse(html);
		Elements elements = doc.getElementsByAttributeValue("name", "authenticity_token");
		Element element2 = elements.get(0);
		String authenticity_token = element2.attr("value");
		return authenticity_token;
	}

	public static List<Map<Object, Object>> collectWorkHour(String html, List<DTO> userList, boolean flag) {
		List<Map<Object, Object>> dtoList = new ArrayList<Map<Object, Object>>();
		for (DTO user : userList) {
			String name = user.getValue("lastname") + " " + user.getValue("familyname");
			Document doc = Jsoup.parse(html);
			Elements eles = doc.getElementsByTag("tbody");
			if (eles == null || eles.isEmpty()) {
				return null;
			}
			Element tbody = eles.get(0);
			Elements trs = tbody.getElementsByTag("tr");
			for (Element ele : trs) {
				Element _td = ele.getElementsByTag("td").get(2);
				if (_td.getElementsByTag("a") != null && !_td.getElementsByTag("a").isEmpty()) {
					DTO dto = new DTO();
					String _name = _td.getElementsByTag("a").get(0).html().toUpperCase();
					if (_name.equals(name.toString().toUpperCase())) {
						Elements tds = ele.getElementsByTag("td");
						int index = 1;
						for (Element _th : tds) {
							if (index == 1 || index == tds.size()) {
								index++;
								continue;
							}
							index++;
							switch (index - 1) {
							case 3:
								dto.addValue("name", getHtml(_th.html()));
								continue;
							case 2:
								dto.addValue("date", getHtml(_th.html()));
								continue;
							case 4:
								dto.addValue("activity", getHtml(_th.html()));
								continue;
							case 5:
								dto.addValue("project", getHtml(_th.html()));
								continue;
							case 6:
								dto.addValue("problem", getHtml(_th.html()));
								dto.addValue("question_id", getHtmlID(_th.html()));
								continue;
							case 7:
								dto.addValue("annotation", getHtml(_th.html()));
								continue;
							case 8:
								dto.addValue("workhour", Double.valueOf(getHtmlWorkHour(_th.html())));
								continue;
							}
						}
						dtoList.add(dto.toMap());
					}
				}
			}
		}
		return dtoList;
	}

	private static Double getHtmlWorkHour(String html) {
		Document doc = Jsoup.parse(html);
		Elements eles = doc.getElementsByTag("span");
		String workhour = eles.get(0).html() + eles.get(1).html();
		return Double.valueOf(workhour);
	}

	private static String getHtmlID(String html) {
		String id = "";
		if (html.indexOf("<a") != -1) {
			int endIndex = html.indexOf("</a>");
			int beginIndex = html.indexOf(">");
			id = html.substring(beginIndex + 1, endIndex);
		} else {
			id = html;
		}

		id = id.substring(id.indexOf("#") + 1, id.length());
		return id;
	}

	public static boolean login(Map<Object, Object> parameterMap) throws ClientProtocolException, IOException {

		HttpPost httpPost = new HttpPost(loginURL);
		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(parameterMap), "UTF-8");
		httpPost.setEntity(postEntity);
		HttpResponse httpResponse = client.execute(httpPost);
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
			String location = httpResponse.getFirstHeader("Location").getValue();
			if (location != null && location.startsWith(loginURL)) {
				System.out.println("----登录失败 .");
				return false;
			} else {
				System.out.println("----登录成功 .");
				return true;
			}

		} else {
			System.out.println("----登录失败 .");
			return false;
		}
	}

	private static List<NameValuePair> getParam(Map<Object, Object> parameterMap) {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		Iterator<Entry<Object, Object>> it = parameterMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> parmEntry = it.next();
			param.add(new BasicNameValuePair((String) parmEntry.getKey(), (String) parmEntry.getValue()));
		}
		return param;
	}

	public static String printResponse(HttpResponse httpResponse) throws ParseException, IOException {
		// 获取响应消息实体
		HttpEntity entity = httpResponse.getEntity();
		// 响应状态
		// System.out.println("status:" + httpResponse.getStatusLine());
		// System.out.println("headers:");
		// HeaderIterator iterator = httpResponse.headerIterator();
		// while (iterator.hasNext()) {
		// System.out.println("\t" + iterator.next());
		// }
		// 判断响应实体是否为空
		if (entity != null) {
			String responseString = EntityUtils.toString(entity);
			// System.out.println("response length:" + responseString.length());
			// System.out.println("response content:" + responseString);
			return responseString;
		}
		return null;
	}

	public static Integer getPageCount(String html) {
		Document doc = Jsoup.parse(html);
		Elements eles = doc.getElementsByClass("pagination");
		Element pagination = eles.get(0);
		Elements as = pagination.getElementsByTag("a");
		for (Element a : as) {
			if (a.html().indexOf("下一页") != -1) {
				String pageCount = a.previousElementSibling().html();
				if (StringUtils.isNotBlank(pageCount)) {
					return Integer.valueOf(pageCount);
				}
			}
		}
		return null;
	}

	public static String getHtml(String html) {
		if (html.indexOf("<a") != -1) {
			int endIndex = html.indexOf("</a>");
			int beginIndex = html.indexOf(">");
			return html.substring(beginIndex + 1, endIndex) + html.substring(html.indexOf("a>") + 2);
		} else {
			return html;
		}
	}

	public static void nextPage(StringBuffer bf, List<Map<Object, Object>> dtoList, Integer pageCount, List<DTO> userList, String type)
			throws ClientProtocolException, IOException {
		for (int i = 2; i <= pageCount; i++) {
			String url = bf.toString().replace("&page=1", "&page=" + i);
			String html = WorkHourHttpUtils.get(url);
			if (!WorkHourHttpUtils.validatorLogin(html)) {
				dtoList.addAll(type.equals("collectWorkHour") ? WorkHourHttpUtils.collectWorkHour(html, userList, false) : WorkHourHttpUtils
						.collectQuestion(html, userList, false));
			}
		}
	}

	public static String post(String url, Map<Object, Object> map) throws ParseException, IOException {
		HttpPost httpPost = new HttpPost(url);
		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(map), "UTF-8");
		httpPost.setEntity(postEntity);
		HttpResponse httpResponse = client.execute(httpPost);
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
			return printResponse(httpResponse);

		} else {
			return printResponse(httpResponse);
		}
	}

	public static Collection<? extends Map<Object, Object>> collectQuestion(String html, List<DTO> userList, boolean b) {
		List<Map<Object, Object>> dtoList = new ArrayList<Map<Object, Object>>();
		for (DTO user : userList) {
			String name = user.getValue("lastname") + " " + user.getValue("familyname");
			Document doc = Jsoup.parse(html);
			Elements eles = doc.getElementsByClass("autoscroll");
			if (eles == null || eles.isEmpty()) {
				return null;
			}
			Elements tables = eles.get(0).children();
			if (tables == null || tables.isEmpty()) {
				return null;
			}
			for (Element tbody : tables.get(0).children()) {
				if (tbody.tagName().toLowerCase().equals("tbody")) {
					for (Element tr : tbody.children()) {
						if (tr.tagName().toLowerCase().equals("tr")) {
							Element _td = tr.getElementsByClass("assigned_to").get(0);
							if (_td.getElementsByTag("a") != null && !_td.getElementsByTag("a").isEmpty()) {
								String _name = _td.getElementsByTag("a").get(0).html().toUpperCase();
								if (_name.equals(name.toString().toUpperCase())) {
									DTO dto = new DTO();
									int index = 1;
									for (Element td : tr.children()) {
										if (index == 1) {
											index++;
											continue;
										}
										index++;
										switch (index - 1) {
										case 2:
											dto.addValue("id", getHtml(td.html()));
											continue;
										case 3:
											dto.addValue("trace", getHtml(td.html()));
											continue;
										case 4:
											dto.addValue("parent", getHtml(td.html()));
											continue;
										case 5:
											dto.addValue("status", getHtml(td.html()));
											continue;
										case 6:
											dto.addValue("priority", getHtml(td.html()));
											continue;
										case 7:
											dto.addValue("theme", getHtml(td.html()));
											continue;
										case 8:
											dto.addValue("assignedto", user.getValue("sname"));
											continue;
										case 9:
											dto.addValue("startdate", getHtml(td.html()));
											continue;
										case 10:
											dto.addValue("plantfinshdate", getHtml(td.html()));
											continue;
										case 11:
											dto.addValue("finshed", getHtmlRemoveTable(td.html()));
											continue;
										case 12:
											dto.addValue("severity", getHtml(td.html()));
											continue;
										}
									}
									dtoList.add(dto.toMap());
								}
							}
						}

					}
				}
			}
		}
		return dtoList;
	}

	public static String getHtmlRemoveTable(String html) {
		Document doc = Jsoup.parse(html);
		Elements tds = doc.getElementsByClass("closed");
		if (tds == null || tds.isEmpty()) {
			return "0";
		}
		String style = tds.get(0).attr("style");
		return style.substring(style.indexOf(":") + 1, style.indexOf("%"));
	}
	
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String url = "http://redmine.china-liantong.com:8000";
		WorkHourHttpUtils.get(url + "/login");
	}

}
