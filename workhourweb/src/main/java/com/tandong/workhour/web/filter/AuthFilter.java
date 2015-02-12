package com.tandong.workhour.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.redmine.ta.beans.User;

import com.tandong.workhour.utils.Constants;
import com.tandong.workhour.utils.PropertyConfigurer;
import com.tandong.workhour.utils.ThreadLocalMap;

public class AuthFilter implements Filter {

	private List<String> filterUrlList;

	public void init(FilterConfig filterConfig) throws ServletException {
		filterUrlList = new ArrayList<String>();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest _request = (HttpServletRequest) request;
		HttpServletResponse _response = (HttpServletResponse) response;
		String requestPath = _request.getRequestURI();

		String noAuthenticationFilterUrl = PropertyConfigurer
				.getContextPropertyStr("workhour.noAuthenticationFilterUrl");
		if (requestPath.matches(noAuthenticationFilterUrl)) {
			chain.doFilter(_request, _response);
			return;
		}

		HttpSession session = _request.getSession(true);
		User user = (User) session.getAttribute(Constants.USER);
		if (user != null) {
			session.setAttribute("user", user);
			ThreadLocalMap.set(user);
			if (requestPath.equals("/workhourweb/")
					|| requestPath.equals("/workhourweb")) {
				String url = PropertyConfigurer
						.getContextPropertyStr("context_url") + "/index";
				_response.sendRedirect(url);
			} else {
				chain.doFilter(_request, _response);
			}
			ThreadLocalMap.clear();
		} else {
			String url = PropertyConfigurer
					.getContextPropertyStr("context_url") + "/login";
			_response.sendRedirect(url);
		}
	}

	public void destroy() {
		filterUrlList.clear();
	}

}