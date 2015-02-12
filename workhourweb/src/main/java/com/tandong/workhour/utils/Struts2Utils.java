package com.tandong.workhour.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class Struts2Utils {
	private static final String ENCODING_PREFIX = "encoding:";
	private static final String NOCACHE_PREFIX = "no-cache:";
	private static final String ENCODING_DEFAULT = "UTF-8";
	private static final boolean NOCACHE_DEFAULT = true;

	private Struts2Utils() {
	}

	public static void render(HttpServletRequest request,HttpServletResponse response,final String contentType, final String content, final String... headers) {
		try {
			String encoding = ENCODING_DEFAULT;
			boolean noCache = NOCACHE_DEFAULT;
			for (String header : headers) {
				String headerName = StringUtils.substringBefore(header, ":");
				String headerValue = StringUtils.substringAfter(header, ":");

				if (StringUtils.equalsIgnoreCase(headerName, ENCODING_PREFIX)) {
					encoding = headerValue;
				} else if (StringUtils.equalsIgnoreCase(headerName, NOCACHE_PREFIX)) {
					noCache = Boolean.parseBoolean(headerValue);
				} else
					throw new IllegalArgumentException(headerName + "head err");
			}

			
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("P3P","CP=CAO PSA OUR");					
//			response.addHeader("Access-Control-Allow-Credentials", "true");
			String fullContentType = contentType + ";charset=" + encoding;
			response.setContentType(fullContentType);
			
			if (noCache) {
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
			}
			
			PrintWriter  out = response.getWriter();
            String cb =request.getParameter("callback");//若果是ajax请求会带这个参数 你可以firfox的firbug跟踪一下就看到了    
            if(cb != null){//如果是跨域  
                StringBuffer sb = new StringBuffer(cb);  
                sb.append("(");  
                sb.append(content);  
                sb.append(")");  
                out.write(sb.toString());  
                out.close();  
            }else{//不跨域的情况  
                out.write(content);  
                out.close();  
            }  
//			response.getWriter().write(content);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void renderText(HttpServletRequest  reuqest, HttpServletResponse response,final String text, final String... headers) {
		render(reuqest,response,"text/plain", text, headers);
	}

	public static void renderHtml(HttpServletRequest  reuqest, HttpServletResponse response,final String html, final String... headers) {
		render(reuqest,response,"text/html", html, headers);
	}
	public static void renderFilterHtml(HttpServletRequest request,HttpServletResponse response,final String html, final String... headers) {
		render(request,response,"text/html", html, headers);
	}
	public static void renderXml(HttpServletRequest  reuqest, HttpServletResponse response,final String xml, final String... headers) {
		render(reuqest,response,"text/xml", xml, headers);
	}

	public static void renderJson(HttpServletRequest  reuqest, HttpServletResponse response,final String string, final String... headers) {
		render(reuqest,response,"application/json", string, headers);
	}
}
