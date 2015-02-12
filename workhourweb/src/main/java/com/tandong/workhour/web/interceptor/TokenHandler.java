package com.tandong.workhour.web.interceptor;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.tandong.workhour.utils.Constants;

public class TokenHandler {

	private static Logger logger = Logger.getLogger(TokenHandler.class);

	static Map<String, String> module_token = null;

	// 生成一个唯一值的token
	@SuppressWarnings("unchecked")
	public synchronized static String generateGUID(HttpSession session) {
		String token = "";
		try {
			Object obj = session.getAttribute("module_token");
			if (obj != null)
				module_token = (Map<String, String>) session.getAttribute("module_token");
			else
				module_token = new HashMap<String, String>();
			token = new BigInteger(165, new Random()).toString(36).toUpperCase();
			module_token.put(Constants.DEFAULT_TOKEN_NAME + "." + token, token);
			session.setAttribute("module_token", module_token);
			Constants.TOKEN_VALUE = token;

		} catch (IllegalStateException e) {
			logger.error("generateGUID() mothod find bug,by token session...");
		}
		return token;
	}

	// 验证表单token值和session中的token值是否一致
	@SuppressWarnings("unchecked")
	public static boolean validToken(HttpServletRequest request) {
		String inputToken = getInputToken(request);

		if (inputToken == null) {
			logger.warn("token is not valid!inputToken is NULL");
			return false;
		}

		HttpSession session = request.getSession();
		Map<String, String> tokenMap = (Map<String, String>) session.getAttribute("module_token");
		if (tokenMap == null || tokenMap.size() < 1) {
			logger.warn("token is not valid!sessionToken is NULL");
			return false;
		}
		String sessionToken = tokenMap.get(Constants.DEFAULT_TOKEN_NAME + "." + inputToken);
		if (!inputToken.equals(sessionToken)) {
			logger.warn("token is not valid!inputToken='" + inputToken + "',sessionToken = '" + sessionToken + "'");
			return false;
		}
		tokenMap.remove(Constants.DEFAULT_TOKEN_NAME + "." + inputToken);
		session.setAttribute("module_token", tokenMap);

		return true;
	}

	// 获取表单中token值
	@SuppressWarnings({ "rawtypes" })
	public static String getInputToken(HttpServletRequest request) {
		Map params = request.getParameterMap();

		if (!params.containsKey(Constants.DEFAULT_TOKEN_NAME)) {
			logger.warn("Could not find token name in params.");
			return null;
		}

		String[] tokens = (String[]) (String[]) params.get(Constants.DEFAULT_TOKEN_NAME);

		if ((tokens == null) || (tokens.length < 1)) {
			logger.warn("Got a null or empty token name.");
			return null;
		}

		return tokens[0];
	}

}
