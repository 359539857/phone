package com.tandong.workhour.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class KeepCookieHttpClinetUtils {

	private static Logger logger = Logger.getLogger(KeepCookieHttpClinetUtils.class);

	private static Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider> create().register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
			.register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory()).build();

	private static HttpClientContext context;

	public static void main(String[] args) throws UnsupportedEncodingException {
		String loginUrl = "http://redmine.china-liantong.com:8000/login";
		Map<Object, Object> loginMap = new HashMap<Object, Object>();
		loginMap.put("username", "petertan");
		loginMap.put("password", "xiaobenzhu520;");

	}

	public static Boolean login(String url, String username, String password, String successFlag) {
		try {
			String responseText = KeepCookieHttpClinetUtils.get(url, username);
			String token = KeepCookieHttpClinetUtils.getAuthenticityToken(responseText);
			Map<Object, Object> parameterMap = new HashMap<Object, Object>();
			parameterMap.put("authenticity_token", token);
			parameterMap.put("username", username);
			parameterMap.put("password", password);
			HttpResponse httpResponse = KeepCookieHttpClinetUtils.postHttpResponse(url, parameterMap, username);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
				String location = httpResponse.getFirstHeader("Location").getValue();
				if (location.equals(successFlag)) {
					logger.info("登录成功");
					printResponse(httpResponse);
					// cookie store
					BasicCookieStore basicCookieStore = KeepCookieHttpClinetUtils.storeCookieStore(httpResponse);
					KeepCookieHttpClinetUtils.shareCookieOnContext(basicCookieStore);
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("登录失败");
		return false;
	}

	public static String get(String url, String username) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		setHeaders(httpGet, null);
		System.out.println("request line:" + httpGet.getRequestLine());
		try {
			// 执行get请求
			HttpResponse httpResponse = client.execute(httpGet, context);
			BasicCookieStore basicCookieStore = KeepCookieHttpClinetUtils.storeCookieStore(httpResponse);
			KeepCookieHttpClinetUtils.shareCookieOnContext(basicCookieStore);
			return printResponse(httpResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流并释放资源
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String post(String url, Map<Object, Object> parameterMap, String username) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		setHeaders(null, httpPost);
		UrlEncodedFormEntity postEntity = null;
		try {
			postEntity = new UrlEncodedFormEntity(getParam(parameterMap), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		httpPost.setEntity(postEntity);
		logger.debug("request line:" + httpPost.getRequestLine());
		try {
			// 执行get请求
			HttpResponse httpResponse = client.execute(httpPost, context);
			BasicCookieStore basicCookieStore = KeepCookieHttpClinetUtils.storeCookieStore(httpResponse);
			KeepCookieHttpClinetUtils.shareCookieOnContext(basicCookieStore);
			return printResponse(httpResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流并释放资源
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static HttpResponse getHttpResponse(String url, String username) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		setHeaders(httpGet, null);
		System.out.println("request line:" + httpGet.getRequestLine());
		try {
			// 执行get请求
			HttpResponse httpResponse = client.execute(httpGet, context);
			BasicCookieStore basicCookieStore = KeepCookieHttpClinetUtils.storeCookieStore(httpResponse);
			KeepCookieHttpClinetUtils.shareCookieOnContext(basicCookieStore);
			return httpResponse;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流并释放资源
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static HttpResponse postHttpResponse(String url, Map<Object, Object> parameterMap, String username) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		setHeaders(null, httpPost);
		UrlEncodedFormEntity postEntity = null;
		try {
			postEntity = new UrlEncodedFormEntity(getParam(parameterMap), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		httpPost.setEntity(postEntity);
		logger.debug("request line:" + httpPost.getRequestLine());
		try {
			// 执行get请求
			HttpResponse httpResponse = client.execute(httpPost, context);
			BasicCookieStore basicCookieStore = KeepCookieHttpClinetUtils.storeCookieStore(httpResponse);
			KeepCookieHttpClinetUtils.shareCookieOnContext(basicCookieStore);
			return httpResponse;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流并释放资源
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void setHeaders(HttpGet get, HttpPost post) {
		if (get != null) {
			get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;");
			get.setHeader("Accept-Language", "zh-cn");
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
			get.setHeader("Accept-Charset", "utf-8");
			get.setHeader("Keep-Alive", "300");
			get.setHeader("Connection", "Keep-Alive");
			get.setHeader("Cache-Control", "no-cache");
			get.setHeader("Host", "redmine.china-liantong.com:8000");

			for (Header header : get.getAllHeaders()) {
				logger.debug("\t" + header.getValue());
			}

		} else if (post != null) {
			post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;");
			post.setHeader("Accept-Language", "zh-cn");
			post.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
			post.setHeader("Accept-Charset", "utf-8");
			post.setHeader("Keep-Alive", "300");
			post.setHeader("Connection", "Keep-Alive");
			post.setHeader("Cache-Control", "no-cache");
			post.setHeader("Host", "redmine.china-liantong.com:8000");

			for (Header header : post.getAllHeaders()) {
				logger.info("\t" + header.getValue());
			}

		}
	}

	public static String printResponse(HttpResponse httpResponse) throws ParseException, IOException {
		// 获取响应消息实体
		HttpEntity entity = httpResponse.getEntity();
		// 响应状态
		logger.debug("status:" + httpResponse.getStatusLine());
		logger.debug("headers:");
		HeaderIterator iterator = httpResponse.headerIterator();
		while (iterator.hasNext()) {
			logger.info("\t" + iterator.next());
		}

		String responseString = null;
		// 判断响应实体是否为空
		if (entity != null && httpResponse.getFirstHeader("Location") == null) {
			responseString = EntityUtils.toString(entity);
			logger.debug("response length:" + responseString.length());
			logger.debug("response content:" + responseString.replace("\r\n", ""));

		}

		return responseString;
	}

	public static HttpClientContext shareCookieOnContext(BasicCookieStore cookieStore) {
		logger.info("----shareCookieOnContext----");
		context = HttpClientContext.create();
		context.setCookieSpecRegistry(registry);
		context.setCookieStore(cookieStore);
		return context;
	}

	public static BasicCookieStore storeCookieStore(HttpResponse httpResponse) {
		logger.info("----storeCookieStore----");
		BasicCookieStore cookieStore = new BasicCookieStore();
		Header[] headers = httpResponse.getHeaders("Set-Cookie");
		for (Header header : headers) {
			String value = header.getValue();
			if (value.indexOf("_redmine_session") != -1) {
				String[] values = value.split(";");
				// 新建一个Cookie
				BasicClientCookie cookie = new BasicClientCookie("_redmine_session", values[0].substring("_redmine_session".length()));
				cookie.setVersion(0);
				cookie.setDomain("redmine.china-liantong.com:8000");
				cookie.setPath(values[1]);
				cookieStore.addCookie(cookie);
				logger.info("_redmine_session:" + value);
				return cookieStore;
			}
		}
		return null;
	}

	public static Cookie getCookie(HttpResponse httpResponse) {
		Header[] headers = httpResponse.getHeaders("Set-Cookie");
		for (Header header : headers) {
			String value = header.getValue();
			if (value.indexOf("_redmine_session") != -1) {
				String[] values = value.split(";");
				// 新建一个Cookie
				Cookie cookie = new Cookie("_redmine_session", values[0].substring("_redmine_session".length()));
				cookie.setVersion(0);
				cookie.setDomain("redmine.china-liantong.com:8000");
				cookie.setPath(values[1]);
				logger.info("_redmine_session:" + value);
				return cookie;
			}
		}
		return null;
	}

	public static List<NameValuePair> getParam(Map<Object, Object> parameterMap) {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		Set<Entry<Object, Object>> set = parameterMap.entrySet();
		Iterator<Entry<Object, Object>> it = set.iterator();
		while (it.hasNext()) {
			Entry<Object, Object> parmEntry = it.next();
			param.add(new BasicNameValuePair(parmEntry.getKey().toString(), parmEntry.getValue().toString()));
		}
		return param;
	}

	public static String getAuthenticityToken(String html) {
		Document doc = Jsoup.parse(html);
		Element element = doc.getElementById("login-form");
		Elements elements = element.getElementsByAttributeValue("name", "authenticity_token");
		Element element2 = elements.get(0);
		String authenticity_token = element2.attr("value");
		return authenticity_token;
	}

	public static String getAuthenticityToken2(String html, String id) {
		Document doc = Jsoup.parse(html);
		Element element = doc.getElementById(id);
		Elements elements = element.getElementsByAttributeValue("name", "authenticity_token");
		Element element2 = elements.get(0);
		String authenticity_token = element2.attr("value");
		return authenticity_token;
	}

	public static String cookieToString(Cookie cookie) {
		StringBuffer bf = new StringBuffer();
		bf.append(cookie.getName());
		bf.append(cookie.getValue());
		// bf.append(cookie.getDomain()).append(";");
		// bf.append(cookie.getPath()).append(";");
		// bf.append("HttpOnly");
		return bf.toString();
	}

}
