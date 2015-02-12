/**
 * 
 */
package com.tandong.workhour.web.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.redmine.ta.RedmineException;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tandong.workhour.entity.DTO;
import com.tandong.workhour.service.CommonService;
import com.tandong.workhour.utils.Constants;
import com.tandong.workhour.utils.KeepCookieHttpClinetUtils;
import com.tandong.workhour.utils.TDEncryptUtils;

/**
 * @author PeterTan
 * 
 */
@Controller
public class LoginAction extends BaseAction {

	@Autowired
	private CommonService<DTO> service;

	@RequestMapping("login")
	public String login(DTO dto) {
		return "login";
	}

	@RequestMapping("auth")
	public String auth(DTO dto) {
		
		long startTime = new Date().getTime();
		try {
			if (!dto.validate("username", "password")) {
				return "redirect:/login?errorText=用户名或密码不能为空";
			}
			RedmineManager mgr = new RedmineManager(redmine_url, dto.getValue(Constants.USER_NAME).toString(), TDEncryptUtils.decodeBase64String(dto.getValue(Constants.PASSWORD)
					.toString()));
			User user = mgr.getCurrentUser();
			user.setLogin(dto.getValue(Constants.USER_NAME).toString());
			user.setPassword(dto.getValue(Constants.PASSWORD).toString());
			Integer count = service.findCount(new DTO().addValue("operatetype", "getUser").addValue("username", dto.getValue("username")));
			if (count <= 0) {
				service.addObject(new DTO().addValue("operatetype", "addUser").addValue("id", user.getId()).addValue("username", user.getLogin())
						.addValue("password", TDEncryptUtils.decodeBase64String(dto.getValue(Constants.PASSWORD).toString())).addValue("lastname", user.getLastName())
						.addValue("familyname", user.getFullName()).addValue("email", user.getMail()));
			} else {
				count = service.findCount(new DTO().addValue("operatetype", "getUserByNameAndPassword").addValue("username", dto.getValue("username"))
						.addValue("password", dto.getValue(Constants.PASSWORD).toString()));
				if (count <= 0) {
					service.updateObject(new DTO().addValue("operatetype", "updateUser").addValue("password", dto.getValue(Constants.PASSWORD).toString())
							.addValue("username", dto.getValue("username")));
				}
			}
			request.getSession(true).setAttribute(Constants.USER, user);
			logger.info(user.getFullName() + "登录成功！");
			KeepCookieHttpClinetUtils.login("http://redmine.china-liantong.com:8000/login", dto.getValue(Constants.USER_NAME).toString(),TDEncryptUtils.decodeBase64String(dto.getValue(Constants.PASSWORD).toString()),"http://redmine.china-liantong.com:8000/my/page");
			response.addCookie(new Cookie(redmine_url, redmine_url));
			long endTime = new Date().getTime();
			logger.info("登录耗时：" + (endTime-startTime) + "毫秒");
		} catch (RedmineException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			return "redirect:/login?errorText=用户名或密码错误";
		} catch (IOException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			return "redirect:/login?errorText=系统故障";
		}
		return "forward:/index?forword=index";
	}

	@RequestMapping("index")
	public String index(DTO dto) {

		User user = (User) request.getSession(true).getAttribute("user");
		List<DTO> sevenhourList = service.findObjectList(new DTO().addValue("operatetype", "getLastSevenDayHourList").addValue("username", user.getLogin()));
		List<DTO> monthhourList = service.findObjectList(new DTO().addValue("operatetype", "getLastMonthDayHourPageList")
				.addValue("page", new DTO().addValue("pageNo", 1).addValue("pageSize", 30)).addValue("username", user.getLogin()));
		request.setAttribute("sevenhourList", sevenhourList);
		request.getSession(true).setAttribute("monthhourList", monthhourList);
		request.setAttribute("dto", dto);
		return dto.getValue("forword");
	}

	@RequestMapping("logout")
	public String logout(DTO dto) {
		User user = (User) request.getSession().getAttribute(Constants.USER);
		logger.info(user.getFullName() + "登录成功！");
		request.getSession().removeAttribute(Constants.USER);
		dto.addCode(Constants.SUCCESS);
		return "redirect:login";
	}

}
