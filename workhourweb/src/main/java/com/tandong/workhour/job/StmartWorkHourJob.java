package com.tandong.workhour.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.TimeEntry;
import org.redmine.ta.beans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tandong.workhour.entity.DTO;
import com.tandong.workhour.service.CommonService;
import com.tandong.workhour.utils.PropertyConfigurer;
import com.tandong.workhour.utils.TDEncryptUtils;
import com.tandong.workhour.utils.WorkHourHttpUtils;

@Component("stmartWorkHourJob")
public class StmartWorkHourJob {

	private static Logger logger = Logger.getLogger(WorkHourJob.class);

	private String uri = PropertyConfigurer.getContextPropertyStr("redmine_url");

	@Autowired
	private CommonService<DTO> service;
	
	
	protected void addHour(){
		/*DTO issue = service.findObject(new DTO());
		long startTime = new Date().getTime();
		final String RE_DEMINE_CONTEXT = "http://redmine.china-liantong.com:8000";
		StringBuffer bf = new StringBuffer(RE_DEMINE_CONTEXT);
		bf.append("/issues/" + issue.getValue("id") + "/time_entries/new");
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
		logger.info("本次工时添加消耗" + (endTime - startTime) + "毫秒");*/
	}
}
