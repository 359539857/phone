/**
 * 
 */
package com.tandong.workhour.plugin;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.tandong.workhour.entity.DTO;

/**
 * @author PeterTan
 * 
 */
public class DTOMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private static Logger logger = Logger.getLogger(DTOMethodArgumentResolver.class);

	private List<String> supportedMediaTypes = Collections.emptyList();

	public DTOMethodArgumentResolver() {
	}

	/**
	 * Construct an {@code AbstractHttpMessageConverter} with one supported
	 * media type.
	 * 
	 * @param supportedMediaType
	 *            the supported media type
	 */
	protected DTOMethodArgumentResolver(String supportedMediaType) {
		setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
	}

	/**
	 * Construct an {@code AbstractHttpMessageConverter} with multiple supported
	 * media type.
	 * 
	 * @param supportedMediaTypes
	 *            the supported media types
	 */
	protected DTOMethodArgumentResolver(String... supportedMediaTypes) {
		setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
	}

	/**
	 * Set the list of {@link MediaType} objects supported by this converter.
	 */
	public void setSupportedMediaTypes(List<String> supportedMediaTypes) {
		Assert.notEmpty(supportedMediaTypes, "'supportedMediaTypes' must not be empty");
		this.supportedMediaTypes = new ArrayList<String>(supportedMediaTypes);
	}

	public List<String> getSupportedMediaTypes() {
		return Collections.unmodifiableList(this.supportedMediaTypes);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Object obj = null;
		try {
			obj = parameter.getParameterType().newInstance();
			if (obj instanceof DTO) {
				return true;
			}

		} catch (InstantiationException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} catch (IllegalAccessException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		Map<?, ?> parameterMap = request.getParameterMap();
		if (null == parameterMap || parameterMap.isEmpty()) {

			if (!this.getSupportedMediaTypes().contains(request.getContentType())) {
				return new DTO();
			}
			// 把reqeust的body读取到StringBuilder
			BufferedReader reader = request.getReader();
			StringBuilder sb = new StringBuilder();

			char[] buf = new char[1024];
			int rd;
			while ((rd = reader.read(buf)) != -1) {
				sb.append(buf, 0, rd);
			}
			if (sb.length() > 0) {
				try {
					return DTO.parseJSONStringToDTO(sb.toString());
				} catch (Exception e) {
					// logger.error(ExceptionUtils.getFullStackTrace(e));
					throw e;
				}
				//
			}
			return new DTO();
		} else {
			return DTO.parseRequestParameterMapToDTO(request.getParameterMap());
		}
	}

}
