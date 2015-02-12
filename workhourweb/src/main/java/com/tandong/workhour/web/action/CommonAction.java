package com.tandong.workhour.web.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.TimeEntry;
import org.redmine.ta.beans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tandong.workhour.entity.DTO;
import com.tandong.workhour.service.CommonService;
import com.tandong.workhour.utils.PropertyConfigurer;
import com.tandong.workhour.utils.TDEncryptUtils;
import com.tandong.workhour.utils.WorkHourHttpUtils;

@Controller
public class CommonAction extends BaseAction {

	@Autowired
	private CommonService<DTO> service;

	private String uri = PropertyConfigurer.getContextPropertyStr("redmine_url");

	@RequestMapping("forword/{pagename}")
	public String forword(@PathVariable String pagename, DTO dto) {
		request.setAttribute("requestDTO", dto);
		return pagename;
	}

	@RequestMapping("addStrategy")
	public String addStrategy(DTO dto) {
		User user = (User) request.getSession().getAttribute("user");
		dto.addValue("username", user.getLogin());
		dto.addValue("operatetype", "addStrategy");
		service.addObject(dto);
		return "forward:/loadData?operatetype=getStrategyPageList&forword=zhineng";
	}

	@RequestMapping("addWork")
	public String addWork(DTO dto) {
		long startTime = new Date().getTime();
		final String RE_DEMINE_CONTEXT = "http://redmine.china-liantong.com:8000";
		StringBuffer bf = new StringBuffer(RE_DEMINE_CONTEXT);
		bf.append("/issues/" + dto.getValue("issueid") + "/time_entries/new");
		try {
			String reponseText = WorkHourHttpUtils.post(bf.toString(), dto.toMap());
			if (WorkHourHttpUtils.validatorLogin(reponseText)) {
				reponseText = WorkHourHttpUtils.get(RE_DEMINE_CONTEXT + "/login");
				User user = (User) request.getSession().getAttribute("user");
				DTO loginDTO = new DTO().addValue("authenticity_token", WorkHourHttpUtils.getAuthenticityToken(reponseText)).addValue("username", user.getLogin())
						.addValue("password", TDEncryptUtils.decodeBase64String(user.getPassword()));

				WorkHourHttpUtils.login(loginDTO.toMap());

				String dengjiUrl = "http://redmine.china-liantong.com:8000/issues/" + dto.getValue("time_entry[issue_id]") + "/time_entries/new";
				reponseText = WorkHourHttpUtils.get(dengjiUrl);

				Map<Object, Object> parameterMap = new HashMap<Object, Object>();
				parameterMap.put("authenticity_token", WorkHourHttpUtils.getToken(reponseText));
				parameterMap.put("time_entry[activity_id]", dto.getValue("activity"));
				parameterMap.put("time_entry[comments]", dto.getValue("comment"));
				parameterMap.put("time_entry[hours]", dto.getValue("hours"));
				parameterMap.put("time_entry[spent_on]", dto.getValue("spentOn"));
				parameterMap.put("time_entry[issue_id]", dto.getValue("issueid"));

				logger.info("正在登记工时信息：" + parameterMap);
				WorkHourHttpUtils.post(RE_DEMINE_CONTEXT + "/projects/" + dto.getValue("projectKey") + "/timelog/create", parameterMap);

				RedmineManager mgr = new RedmineManager(redmine_url, user.getLogin(), TDEncryptUtils.decodeBase64String(user.getPassword()));
				List<TimeEntry> timeEntryList = mgr.getTimeEntriesForIssue(Integer.valueOf(dto.getValue("time_entry[issue_id]").toString()));
				if (timeEntryList != null && !timeEntryList.isEmpty()) {
					for (TimeEntry entry : timeEntryList) {
						if (entry.getUserId().equals(user.getId())) {
							try {
								int count = service.findCount(new DTO().addValue("operatetype", "getHour").addValue("id", entry.getId()));
								if (count <= 0) {
									service.addObject(new DTO().addValue("operatetype", "addHour").addValue("id", entry.getId()).addValue("issueId", entry.getIssueId())
											.addValue("projectId", entry.getProjectId()).addValue("projectName", entry.getProjectName()).addValue("userName", user.getLogin())
											.addValue("activityName", entry.getActivityName()).addValue("hour", entry.getHours()).addValue("comment", entry.getComment())
											.addValue("createdOn", entry.getCreatedOn()).addValue("spentOn", entry.getSpentOn()).addValue("updatedOn", entry.getUpdatedOn()));
								}
							} catch (Exception e) {
								logger.error(ExceptionUtils.getFullStackTrace(e));
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		long endTime = new Date().getTime();
		logger.info("本次工时添加消耗" + (endTime - startTime) + "毫秒");
		return "forward:/loadData?operatetype=issuetimedetail&issueID=" + dto.getValue("issueid");
	}

	@RequestMapping("loadData")
	public String loadData(DTO dto) {
		String operatetype = dto.getValue("operatetype");
		User user = (User) request.getSession().getAttribute("user");
		dto.addValue("username", user.getLogin());
		dto.addValue("userid", user.getId());
		dto.addValue("userid2", user.getId());
		try {
			Object obj = null;
			if (operatetype.endsWith("issuetimedetail")) {
				RedmineManager mgr = new RedmineManager(redmine_url, user.getLogin(), TDEncryptUtils.decodeBase64String(user.getPassword()));
				obj = mgr.getTimeEntriesForIssue(Integer.valueOf(dto.getValue("issueID").toString()));
			} else if (operatetype.endsWith("Count")) {
				obj = service.findCount(dto);
			} else if (operatetype.endsWith("Object")) {
				obj = service.findObject(dto);
			} else if (operatetype.endsWith("ComplexList")) {
				if (operatetype.indexOf("Page") != -1) {
					DTO page = new DTO();
					if (dto.containsKey("pageNo") && dto.getValue("pageNo") != null) {
						page.addValue("pageNo", Integer.valueOf(dto.getValue("pageNo").toString()));
					} else {
						page.addValue("pageNo", 1);
					}
					if (dto.containsKey("pageSize") && dto.getValue("pageSize") != null) {
						page.addValue("pageSize", Integer.valueOf(dto.getValue("pageSize").toString()));
					} else {
						page.addValue("pageSize", 6);
					}

					dto.addValue("page", page);
				}
				Integer totalRecord = service.findCount(dto);
				((DTO) dto.getValue("page")).addValue("totalRecord", totalRecord);
				Integer pageSize = dto.getPointValue("page.pageSize");
				((DTO) dto.getValue("page")).addValue("totalPage", totalRecord % pageSize == 0 ? totalRecord / pageSize : totalRecord / pageSize + 1);
				((DTO) dto.getValue("page")).addValue("offset", (Integer.valueOf(dto.getPointValue("pageNo").toString()) - 1) * pageSize);
				obj = service.findObjectList(dto);
			} else {
				if (operatetype.indexOf("Page") != -1) {
					DTO page = new DTO();
					if (dto.containsKey("pageNo") && dto.getValue("pageNo") != null) {
						page.addValue("pageNo", Integer.valueOf(dto.getValue("pageNo").toString()));
					} else {
						page.addValue("pageNo", 1);
					}
					if (dto.containsKey("pageSize") && dto.getValue("pageSize") != null) {
						page.addValue("pageSize", Integer.valueOf(dto.getValue("pageSize").toString()));
					} else {
						page.addValue("pageSize", 10);
					}

					dto.addValue("page", page);
				}
				obj = service.findObjectList(dto);
			}
			request.setAttribute("requestDTO", obj);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		request.setAttribute("dto", dto);
		if (!dto.containsKey("forword")) {
			dto.addValue("forword", dto.getValue("operatetype"));
		}
		return dto.getValue("forword");
	}

}
