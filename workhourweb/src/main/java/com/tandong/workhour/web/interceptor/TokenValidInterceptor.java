package com.tandong.workhour.web.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.tandong.workhour.entity.DTO;

public class TokenValidInterceptor implements HandlerInterceptor {

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Map<Object, Object> paramMap = request.getParameterMap();
		if (paramMap != null && !paramMap.isEmpty()) {
			DTO dto = DTO.parseRequestParameterMapToDTO(request.getParameterMap());
			if (dto.containsKey("module_token")) {
				if (!TokenHandler.validToken(request)) {
					response.getWriter().println("<script>history.back();</script>");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// TODO Auto-generated method stub

	}

}
